package com.jinhx.blog.service.article.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.config.params.ParamsHttpServletRequestWrapper;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.*;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.dto.ArticleDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;
import com.jinhx.blog.mapper.article.ArticleMapper;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.CategoryService;
import com.jinhx.blog.service.operation.RecommendService;
import com.jinhx.blog.service.operation.TagService;
import com.jinhx.blog.service.operation.TopService;
import com.jinhx.blog.service.sys.SysUserService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * articleAdminServiceImpl
 * @author jinhx
 * @date 2018/11/21 12:48
 * @description
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    // 每天重新计算点赞，key
    private static final String BLOG_ARTICLE_LIKE_LOCK_KEY = "blog:article:like:lock:";

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private CacheServer cacheServer;

    @Autowired
    private TopService topService;

    @Autowired
    private SysUserService sysUserService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 获取首页信息
     * @return 首页信息
     */
    @Override
    public HomeArticleInfoVO getHomeArticleInfoVO() {
        Integer publishCount = baseMapper.selectPublishCount();
        Integer allCount = baseMapper.selectAllCount();

        HomeArticleInfoVO homeArticleInfoVO = new HomeArticleInfoVO();
        homeArticleInfoVO.setPublishCount(publishCount);
        homeArticleInfoVO.setAllCount(allCount);
        return homeArticleInfoVO;
    }

    /**
     * 分页查询文章列表
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 文章列表
     */
    @Override
    public PageUtils queryPage(Integer page, Integer limit, String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("title", title);

        Page<ArticleDTO> articleDTOPage = new Query<ArticleDTO>(params).getPage();
        List<ArticleDTO> articleDTOList = baseMapper.listArticleDTO(articleDTOPage, params);
        // 查询所有分类
        List<Category> categoryList = categoryService.list(new QueryWrapper<Category>().lambda().eq(Category::getModule, ModuleTypeConstants.ARTICLE));
        // 封装ArticleVo
        List<ArticleVO> articleVOList = new ArrayList<>();
        Optional.ofNullable(articleDTOList).ifPresent((articleDTOs ->
                articleDTOs.forEach(articleDTO -> {
                    // 设置类别
                    articleDTO.setCategoryListStr(categoryService.renderCategoryArr(articleDTO.getCategoryId(), categoryList));
                    // 设置标签列表
                    articleDTO.setTagList(tagService.listByLinkId(articleDTO.getId(), ModuleTypeConstants.ARTICLE));

                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(articleDTO, articleVO);
                    if (recommendService.selectRecommendByLinkIdAndType(articleDTO.getId(), ModuleTypeConstants.ARTICLE) != null) {
                        articleVO.setRecommend(true);
                    } else {
                        articleVO.setRecommend(false);
                    }

                    articleVO.setAuthor(sysUserService.getNicknameByUserId(articleDTO.getCreaterId()));

                    articleVOList.add(articleVO);
                })));
        Page<ArticleVO> articleVOPage = new Page<>();
        BeanUtils.copyProperties(articleDTOPage, articleVOPage);
        articleVOPage.setRecords(articleVOList);
        return new PageUtils(articleVOPage);
    }

    /**
     * 保存文章
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleVO articleVO) {
        baseMapper.insert(articleVO);
        tagService.saveTagAndNew(articleVO.getTagList(),articleVO.getId(), ModuleTypeConstants.ARTICLE);
        // 当文章是发布状态时，需要新增到es中
        if (articleVO.getPublish()){
            InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
            initGitalkRequest.setId(articleVO.getId());
            initGitalkRequest.setTitle(articleVO.getTitle());
            initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY, JsonUtils.objectToJson(articleVO));
        }

        cleanArticlesCache(new Integer[]{});
    }

    /**
     * 更新文章
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleVO articleVO) {
        Article article = baseMapper.selectById(articleVO.getId());
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        // 删除多对多所属标签
        tagService.deleteTagLink(articleVO.getId(), ModuleTypeConstants.ARTICLE);
        // 更新所属标签
        tagService.saveTagAndNew(articleVO.getTagList(),articleVO.getId(), ModuleTypeConstants.ARTICLE);
        baseMapper.updateArticleById(articleVO);
        if (articleVO.getRecommend() != null){
            if (articleVO.getRecommend()){
                if (recommendService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) == null){
                    Integer maxOrderNum = recommendService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendService.insertRecommend(recommend);
                }
            }else {
                if (recommendService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null){
                    recommendService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleVO.getId()), ModuleTypeConstants.ARTICLE);
                }
            }
        }
        // 发送rabbitmq消息同步到es
        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(articleVO.getId());
        initGitalkRequest.setTitle(articleVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));

        if (article.getPublish() && articleVO.getPublish()){
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY, JsonUtils.objectToJson(articleVO));
        }else if (article.getPublish() && !articleVO.getPublish()){
            Integer[] ids = {articleVO.getId()};
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
        }else if (!article.getPublish() && articleVO.getPublish()){
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY, JsonUtils.objectToJson(articleVO));
        }

        cleanArticlesCache(new Integer[]{articleVO.getId()});
    }

    /**
     * 更新文章状态
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleStatus(ArticleVO articleVO) {
        Article article = baseMapper.selectById(articleVO.getId());
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        if (articleVO.getPublish() != null){
            // 更新发布，公开状态
            baseMapper.updateArticleById(articleVO);
            if (article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY, JsonUtils.objectToJson(baseMapper.selectArticleById(articleVO.getId())));
            }else if (article.getPublish() && !articleVO.getPublish()){
                Integer[] ids = {articleVO.getId()};
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
            }else if (!article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY, JsonUtils.objectToJson(baseMapper.selectArticleById(articleVO.getId())));
            }
        }else if (articleVO.getOpen() != null){
            // 更新公开状态
            baseMapper.updateArticleById(articleVO);
            if (article.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY, JsonUtils.objectToJson(baseMapper.selectArticleById(articleVO.getId())));
            }
        }

        if (articleVO.getRecommend() != null){
            // 更新推荐状态
            if (articleVO.getRecommend()){
                if (recommendService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) == null){
                    Integer maxOrderNum = recommendService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendService.insertRecommend(recommend);
                }
            }else {
                if (recommendService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null){
                    recommendService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleVO.getId()), ModuleTypeConstants.ARTICLE);
                }
            }
        }

        cleanArticlesCache(new Integer[]{articleVO.getId()});
    }

    /**
     * 根据文章id获取文章信息
     * @param articleId 文章id
     * @return 文章信息
     */
    @Override
    public ArticleVO getArticle(Integer articleId) {
        Article article = baseMapper.selectById(articleId);
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!ObjectUtils.equals(SysAdminUtils.getUserId(), article.getCreaterId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "后台查看未公开的文章只能由创建者查看");
        }

        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        // 查询所属标签
        articleVO.setTagList(tagService.listByLinkId(articleId, ModuleTypeConstants.ARTICLE));
        Recommend recommend = recommendService.selectRecommendByLinkIdAndType(articleId, ModuleTypeConstants.ARTICLE);
        if (recommend != null){
            articleVO.setRecommend(true);
        }else {
            articleVO.setRecommend(false);
        }

        articleVO.setAuthor(sysUserService.getNicknameByUserId(article.getCreaterId()));

        return articleVO;
    }

    /**
     * 查看未公开文章时检测密码是否正确
     * @param articleId 文章id
     * @param password 密码
     */
    @Override
    public void checkPassword(Integer articleId, String password) {
        Article article = baseMapper.selectById(articleId);
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!ObjectUtils.equals(SysAdminUtils.getUserId(), article.getCreaterId()) && !article.getOpen()){
            if (StringUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserDTO sysUserDTO = sysUserService.getSysUserDTOByUserId(article.getCreaterId());
            if (sysUserDTO == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserDTO.getPassword().equals(new Sha256Hash(password, sysUserDTO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }
    }

    /**
     * 判断类别下是否有文章
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkByCategory(Integer categoryId) {
        return baseMapper.checkByCategory(categoryId) > 0;
    }

    /**
     * 判断上传文件下是否有文章
     * @param url
     * @return
     */
    @Override
    public boolean checkByFile(String url) {
        return baseMapper.checkByFile(url) > 0;
    }

    /**
     * 批量删除
     * @param ids 文章id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticles(Integer[] ids) {
        Arrays.stream(ids).forEach(articleId -> {
            Article article = baseMapper.selectById(articleId);
            if (article == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
            }
            if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者删除");
            }

            //先删除博文标签多对多关联
            tagService.deleteTagLink(articleId, ModuleTypeConstants.ARTICLE);
            baseMapper.deleteBatchIds(Arrays.asList(articleId));

            recommendService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleId), ModuleTypeConstants.ARTICLE);
        });

        // 发送rabbitmq消息同步到es
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));

        cleanArticlesCache(ids);
    }

    /**
     * 清除缓存
     */
    private void cleanArticlesCache(Integer[] ids){
        taskExecutor.execute(() ->{
            cacheServer.cleanArticlesCache(ids);
        });
    }

    /********************** portal ********************************/

    /**
     * 分页获取文章列表
     * @param page 页码
     * @param limit 每页数量
     * @param categoryId 分类
     * @param latest 时间排序
     * @param like 点赞量排序
     * @param read 阅读量排序
     * @return 文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES)
    @Override
    public PageUtils listArticles(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean read) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("latest", latest);
        params.put("like", like);
        params.put("read", read);
        if (categoryId != null){
            params.put("categoryId", String.valueOf(categoryId));
        }

        Page<ArticleDTO> articleDTOPage = new Query<ArticleDTO>(params).getPage();
        List<ArticleDTO> articleList = baseMapper.queryPageCondition(articleDTOPage, params);
        if (articleList == null){
            articleList = new ArrayList<>();
        }

        articleList.forEach(articleListItem -> {
            articleListItem.setAuthor(sysUserService.getNicknameByUserId(articleListItem.getCreaterId()));
        });

        articleDTOPage.setRecords(articleList);
        return new PageUtils(articleDTOPage);
    }

    /**
     * 分页获取首页文章列表
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES)
    @Override
    public PageUtils listHomeArticles(Integer page, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));

        Page<ArticleVO> articleVOPage = new Query<ArticleVO>(params).getPage();
        List<ArticleDTO> articleList = baseMapper.queryHomePageCondition(articleVOPage, params);
        if (articleList == null){
            articleList = new ArrayList<>();
        }

        List<TopVO> topVOs = topService.listTopVO(ModuleTypeConstants.ARTICLE);
        ArticleVO[] articleVOS = new ArticleVO[articleList.size()];
        if (!CollectionUtils.isEmpty(topVOs) && !CollectionUtils.isEmpty(articleList)){
            topVOs.forEach(topVOsItem -> {
                if (topVOsItem.getOrderNum() > (page - 1) * limit && topVOsItem.getOrderNum() < page * limit){
                    Article article = baseMapper.selectArticleById(topVOsItem.getLinkId());
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(article, articleVO);
                    articleVO.setTop(true);
                    articleVO.setTagList(tagService.listByLinkId(topVOsItem.getLinkId(), ModuleTypeConstants.ARTICLE));
                    articleVO.setAuthor(sysUserService.getNicknameByUserId(articleVO.getCreaterId()));
                    articleVOS[(topVOsItem.getOrderNum() - (page - 1) * limit) -1] = articleVO;
                }
            });
            Queue<ArticleDTO> articleDTOQueue = new LinkedList<>(articleList);
            for (int i = 0; i < articleVOS.length; i++) {
                if (articleVOS[i] == null){
                    ArticleDTO articleDTO = articleDTOQueue.poll();
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(articleDTO, articleVO);
                    articleVOS[i] = articleVO;
                }
            }
        }

        // 封装ArticleVo
        articleVOPage.setRecords(Arrays.asList(articleVOS));
        return new PageUtils(articleVOPage);
    }

    /**
     * 获取ArticleDTO对象
     * @param id id
     * @param password password
     * @return ArticleDTO
     */
    @Cacheable(value = RedisKeyConstants.ARTICLE, key = "#id + ':' + #password")
    @Override
    public ArticleDTO getArticleDTO(Integer id, String password) {
        Article article = baseMapper.selectById(id);
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!ObjectUtils.equals(SysAdminUtils.getUserId(), article.getCreaterId()) && !article.getOpen()){
            if (StringUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserDTO sysUserDTO = sysUserService.getSysUserDTOByUserId(article.getCreaterId());
            if (sysUserDTO == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserDTO.getPassword().equals(new Sha256Hash(password, sysUserDTO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }

        ArticleDTO articleDTO = new ArticleDTO();
        BeanUtils.copyProperties(article, articleDTO);
        articleDTO.setTagList(tagService.listByLinkId(id, ModuleTypeConstants.ARTICLE));
        articleDTO.setAuthor(sysUserService.getNicknameByUserId(articleDTO.getCreaterId()));
        // 浏览数量
        baseMapper.updateReadNum(id);
        return articleDTO;
    }

    /**
     * 获取热读榜
     * @return 热读文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES, key = "'hostread'")
    @Override
    public List<ArticleVO> listHotReadArticles() {
        List<ArticleDTO> articleDTOList = baseMapper.getHotReadList();
        List<ArticleVO> articleVOList = new ArrayList<>();
        articleDTOList.forEach(articleDTOListItem -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(articleDTOListItem, articleVO);
            articleVOList.add(articleVO);
        });
        return articleVOList;
    }

    /**
     * 文章点赞
     * @param id id
     * @return 点赞结果
     */
    @Override
    public Boolean updateArticle(Integer id) throws Exception {
        //获取request
        ParamsHttpServletRequestWrapper request = (ParamsHttpServletRequestWrapper) HttpContextUtils.getHttpServletRequest();
        String userId = EncodeUtils.encoderByMD5(IPUtils.getIpAddr(request) + UserAgentUtils.getBrowserName(request) +
                UserAgentUtils.getBrowserVersion(request) + UserAgentUtils.getDeviceManufacturer(request) +
                UserAgentUtils.getDeviceType(request) + UserAgentUtils.getOsVersion(request));
        // 每天重新计算点赞
        if (!redisUtils.setIfAbsent(BLOG_ARTICLE_LIKE_LOCK_KEY + userId + ":" + id, "1", DateUtils.getRemainMilliSecondsOneDay())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "1天只能点赞1次，请明天再来点赞");
        }

        return baseMapper.updateLikeNum(id);
    }

    /**
     * 根据文章id获取公开状态
     * @param articleId 文章id
     * @return 公开状态
     */
    @Override
    public Boolean getArticleOpenById(Integer articleId) {
        Article article = baseMapper.selectById(articleId);
        if (article == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        return article.getOpen();
    }

}
