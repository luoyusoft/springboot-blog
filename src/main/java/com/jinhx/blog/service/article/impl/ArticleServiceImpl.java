package com.jinhx.blog.service.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.adaptor.article.ArticleAdaptor;
import com.jinhx.blog.adaptor.article.ArticleAdaptorBuilder;
import com.jinhx.blog.common.config.params.ParamsHttpServletRequestWrapper;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.*;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;
import com.jinhx.blog.mapper.article.ArticleMapper;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.RecommendService;
import com.jinhx.blog.service.operation.TagLinkService;
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
 * ArticleServiceImpl
 *
 * @author jinhx
 * @since 2018-11-21
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    /**
     * 每天重新计算点赞，key
     */
    private static final String BLOG_ARTICLE_LIKE_LOCK_KEY = "blog:article:like:lock:";

    @Autowired
    private TagService tagService;

    @Autowired
    private TagLinkService tagLinkService;

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

    @Autowired
    private ArticleAdaptor articleAdaptor;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeArticleInfoVO getHomeArticleInfoVO() {
        Integer publishCount = baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE));
        Integer allCount = baseMapper.selectCount(new LambdaQueryWrapper<>());

        HomeArticleInfoVO homeArticleInfoVO = new HomeArticleInfoVO();
        homeArticleInfoVO.setPublishCount(publishCount);
        homeArticleInfoVO.setAllCount(allCount);
        return homeArticleInfoVO;
    }

    /**
     * 分页查询文章列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 文章列表
     */
    @Override
    public PageUtils queryPage(Integer page, Integer limit, String title) {
        IPage<Article> articleIPage = baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(ObjectUtil.isNotEmpty(title), Article::getTitle, title)
                .orderByDesc(Article::getUpdateTime)
                .select(Article::getId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageUtils(articleIPage);
        }

        List<ArticleVO> articleVOs = articleAdaptor.adaptorArticlesToArticleVOs(new ArticleAdaptorBuilder.Builder<List<Article>>()
                .setCategoryListStr()
                .setTagList()
                .setRecommend()
                .setAuthor()
                .build(articleIPage.getRecords()));
        IPage<ArticleVO> articleVOIPage = new Page<>();
        BeanUtils.copyProperties(articleIPage, articleVOIPage);
        articleVOIPage.setRecords(articleVOs);
        return new PageUtils(articleVOIPage);
    }

    /**
     * 保存文章
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleVO articleVO) {
        Article article = articleAdaptor.adaptorArticleVOToArticle(new ArticleAdaptorBuilder.Builder<ArticleVO>()
                .build(articleVO));
        baseMapper.insert(article);
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
     * 批量删除
     *
     * @param ids 文章id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticles(Integer[] ids) {
        Arrays.stream(ids).forEach(articleId -> {
            Article article = baseMapper.selectById(articleId);
            if (Objects.isNull(article)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
            }
            if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者删除");
            }

            //先删除博文标签多对多关联
            tagLinkService.deleteTagLink(articleId, ModuleTypeConstants.ARTICLE);
            baseMapper.deleteBatchIds(Arrays.asList(articleId));

            recommendService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleId), ModuleTypeConstants.ARTICLE);
        });

        // 发送rabbitmq消息同步到es
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));

        cleanArticlesCache(ids);
    }

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleVO articleVO) {
        Article article = baseMapper.selectById(articleVO.getId());
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        // 删除多对多所属标签
        tagLinkService.deleteTagLink(articleVO.getId(), ModuleTypeConstants.ARTICLE);
        // 更新所属标签
        tagService.saveTagAndNew(articleVO.getTagList(),articleVO.getId(), ModuleTypeConstants.ARTICLE);
        baseMapper.updateById(articleVO);
        if (!Objects.isNull(articleVO.getRecommend())){
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
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleStatus(ArticleVO articleVO) {
        Article article = baseMapper.selectById(articleVO.getId());
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!ObjectUtils.equals(article.getCreaterId(), SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        if (!Objects.isNull(articleVO.getPublish())){
            // 更新发布，公开状态
            baseMapper.updateById(articleVO);
            if (article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(baseMapper.selectOne(new LambdaQueryWrapper<Article>()
                                .eq(Article::getId, articleVO.getId())
                                .eq(Article::getPublish, Article.PUBLISH_TRUE))));
            }else if (article.getPublish() && !articleVO.getPublish()){
                Integer[] ids = {articleVO.getId()};
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
            }else if (!article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                        JsonUtils.objectToJson(baseMapper.selectOne(new LambdaQueryWrapper<Article>()
                                .eq(Article::getId, articleVO.getId())
                                .eq(Article::getPublish, Article.PUBLISH_TRUE))));
            }
        }else if (articleVO.getOpen() != null){
            // 更新公开状态
            baseMapper.updateById(articleVO);
            if (article.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(baseMapper.selectOne(new LambdaQueryWrapper<Article>()
                                .eq(Article::getId, articleVO.getId())
                                .eq(Article::getPublish, Article.PUBLISH_TRUE))));
            }
        }

        if (!Objects.isNull(articleVO.getRecommend())){
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
     *
     * @param articleId 文章id
     * @param publish publish
     * @return 文章信息
     */
    @Override
    public ArticleVO getArticleVO(Integer articleId, Boolean publish) {
        Article article = getArticle(articleId, publish);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!ObjectUtils.equals(SysAdminUtils.getUserId(), article.getCreaterId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "后台查看未公开的文章只能由创建者查看");
        }

        return articleAdaptor.adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                .setCategoryListStr()
                .setTagList()
                .setRecommend()
                .setAuthor()
                .build(article));
    }

    /**
     * 根据文章id获取文章信息
     *
     * @param articleId 文章id
     * @param publish publish
     * @return 文章信息
     */
    @Override
    public Article getArticle(Integer articleId, Boolean publish) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, articleId)
                .eq(publish != null, Article::getPublish, publish));
    }

    /**
     * 查看未公开文章时检测密码是否正确
     *
     * @param articleId 文章id
     * @param password 密码
     */
    @Override
    public void checkPassword(Integer articleId, String password) {
        Article article = baseMapper.selectById(articleId);
        if (Objects.isNull(article)){
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
     *
     * @param categoryId categoryId
     * @return 是否有文章
     */
    @Override
    public boolean checkByCategoryId(Integer categoryId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .like(categoryId != null, Article::getCategoryId, categoryId)) > 0;
    }

    /**
     * 判断上传文件下是否有文章
     *
     * @param url url
     * @return 是否有文章
     */
    @Override
    public boolean checkByFile(String url) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(ObjectUtil.isNotEmpty(url), Article::getCover, url)) > 0;
    }

    /**
     * 查询所有已发布的文章
     *
     * @return 所有已发布的文章
     */
    @Override
    public List<Article> listArticlesByPublish() {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE));
    }

    /**
     * 根据标题查询所有已发布的文章
     *
     * @param title 标题
     * @return 所有已发布的文章
     */
    @Override
    public List<Article> listArticlesByPublishAndTitle(String title) {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .like(ObjectUtil.isNotEmpty(title), Article::getTitle, title)
                .orderByDesc(Article::getId));
    }

    /**
     * 清除缓存
     *
     * @param ids ids
     * */
    private void cleanArticlesCache(Integer[] ids){
        taskExecutor.execute(() ->{
            cacheServer.cleanArticlesCache(ids);
        });
    }

    /********************** portal ********************************/

    /**
     * 分页获取文章列表
     *
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
        IPage<Article> articleIPage = baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .like(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(latest, Article::getCreateTime)
                .orderByDesc(like, Article::getLikeNum)
                .orderByDesc(read, Article::getReadNum));

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageUtils(articleIPage);
        }

        List<ArticleVO> articleVOs = articleAdaptor.adaptorArticlesToArticleVOs(new ArticleAdaptorBuilder.Builder<List<Article>>()
                .setTagList()
                .setAuthor()
                .build(articleIPage.getRecords()));
        IPage<ArticleVO> articleVOIPage = new Page<>();
        BeanUtils.copyProperties(articleIPage, articleVOIPage);
        articleVOIPage.setRecords(articleVOs);
        return new PageUtils(articleVOIPage);
    }

    /**
     * 分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES)
    @Override
    public PageUtils listHomeArticles(Integer page, Integer limit) {
        List<Integer> linkIds = recommendService.selectLinkIdsByModule(ModuleTypeConstants.ARTICLE);

        IPage<Article> articleIPage = baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .notIn(!CollectionUtils.isEmpty(linkIds), Article::getId)
                .orderByDesc(Article::getCreateTime));

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageUtils(articleIPage);
        }

        List<TopVO> topVOs = topService.listTopVO(ModuleTypeConstants.ARTICLE);
        ArticleVO[] articleVOs = new ArticleVO[articleIPage.getRecords().size()];
        if (!CollectionUtils.isEmpty(topVOs)){
            topVOs.forEach(topVOsItem -> {
                if (topVOsItem.getOrderNum() > (page - 1) * limit && topVOsItem.getOrderNum() < page * limit){
                    Article article = baseMapper.selectOne(new LambdaUpdateWrapper<Article>()
                            .eq(Article::getId, topVOsItem.getLinkId())
                            .eq(Article::getPublish, Article.PUBLISH_TRUE));

                    if (Objects.isNull(article)){
                        return;
                    }

                    ArticleVO articleVO = articleAdaptor.adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                            .setTagList()
                            .setAuthor()
                            .build(article));
                    articleVO.setTop(Article.TOP_TRUE);
                    articleVOs[(topVOsItem.getOrderNum() - (page - 1) * limit) -1] = articleVO;
                }
            });

            Queue<Article> articleQueue = new LinkedList<>(articleIPage.getRecords());
            for (int i = 0; i < articleVOs.length; i++) {
                if (Objects.isNull(articleVOs[i])){
                    articleVOs[i] = articleAdaptor.adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                            .setTagList()
                            .setAuthor()
                            .build(articleQueue.poll()));
                }
            }
        }

        IPage<ArticleVO> articleVOIPage = new Page<>();
        BeanUtils.copyProperties(articleIPage, articleVOIPage);
        articleVOIPage.setRecords(Arrays.asList(articleVOs));
        return new PageUtils(articleVOIPage);
    }

    /**
     * 获取ArticleVO对象
     *
     * @param id id
     * @param password password
     * @return ArticleVO
     */
    @Cacheable(value = RedisKeyConstants.ARTICLE, key = "#id + ':' + #password")
    @Override
    public ArticleVO getArticleVOByPassword(Integer id, String password) {
        Article article = baseMapper.selectById(id);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!ObjectUtils.equals(SysAdminUtils.getUserId(), article.getCreaterId()) && !article.getOpen()){
            if (StringUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserDTO sysUserDTO = sysUserService.getSysUserDTOByUserId(article.getCreaterId());
            if (Objects.isNull(sysUserDTO)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserDTO.getPassword().equals(new Sha256Hash(password, sysUserDTO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }

        ArticleVO articleVO = articleAdaptor.adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                .setTagList()
                .setAuthor()
                .build(article));
        // 浏览数量
        baseMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, article.getId())
                .setSql("read_num = read_num + 1"));
        return articleVO;
    }

    /**
     * 获取热读榜
     *
     * @return 热读文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES, key = "'hostread'")
    @Override
    public List<ArticleVO> listHotReadArticles() {
        List<Article> articles = baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .eq(Article::getOpen, Article.OPEN_TRUE)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 5")
                .orderByDesc(Article::getReadNum)
                .select(Article::getId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));

        return articleAdaptor.adaptorArticlesToArticleVOs(new ArticleAdaptorBuilder.Builder<List<Article>>()
                .build(articles));
    }

    /**
     * 文章点赞
     *
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

        return baseMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .setSql("like_num = like_num + 1")) > 0;
    }

    /**
     * 根据文章id获取公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    @Override
    public Boolean getArticleOpenById(Integer articleId) {
        Article article = baseMapper.selectById(articleId);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        return article.getOpen();
    }

}
