package com.jinhx.blog.service.article.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.filter.params.ParamsHttpServletRequestWrapper;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.common.util.*;
import com.jinhx.blog.engine.article.ArticleEngine;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.engine.article.flow.ArticleVOsQueryFlow;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleAdaptorBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;
import com.jinhx.blog.mapper.article.ArticleMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.*;
import com.jinhx.blog.service.sys.SysUserMapperService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    private ArticleMapperService articleMapperService;

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Autowired
    private CacheServer cacheServer;

    @Autowired
    private TopMapperService topMapperService;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private CategoryMapperService categoryMapperService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Autowired
    private ArticleEngine articleEngine;

    /**
     * 将Article按需转换为ArticleVO
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return ArticleVO
     */
    @Override
    public ArticleVO adaptorArticleToArticleVO(ArticleAdaptorBuilder<Article> articleAdaptorBuilder){
        if(Objects.isNull(articleAdaptorBuilder) || Objects.isNull(articleAdaptorBuilder.getData())){
            return null;
        }

        Article article = articleAdaptorBuilder.getData();
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);

        if (articleAdaptorBuilder.getCategoryListStr()){
            List<Category> categorys = categoryMapperService.list(new LambdaQueryWrapper<Category>().eq(Category::getModule, ModuleTypeConstants.ARTICLE));

            if(!CollectionUtils.isEmpty(categorys)){
                articleVO.setCategoryListStr(categoryMapperService.renderCategoryArr(articleVO.getCategoryId(), categorys));
            }
        }

        if (articleAdaptorBuilder.getTagList()){
            List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(articleVO.getId(), ModuleTypeConstants.ARTICLE);
            if (!CollectionUtils.isEmpty(tagLinks)){
                articleVO.setTagList(tagMapperService.listByLinkId(tagLinks));
            }
        }

        if (articleAdaptorBuilder.getRecommend()){
            articleVO.setRecommend(recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null);
        }

        if (articleAdaptorBuilder.getTop()){
            articleVO.setTop(topMapperService.isTopByModuleAndLinkId(ModuleTypeConstants.ARTICLE, articleVO.getId()));
        }

        if (articleAdaptorBuilder.getAuthor()){
            articleVO.setAuthor(sysUserMapperService.getNicknameByUserId(articleVO.getCreaterId()));
        }

        return articleVO;
    }

    /**
     * 将ArticleVO转换为Article
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return Article
     */
    @Override
    public Article adaptorArticleVOToArticle(ArticleAdaptorBuilder<ArticleVO> articleAdaptorBuilder){
        if(Objects.isNull(articleAdaptorBuilder) || Objects.isNull(articleAdaptorBuilder.getData())){
            return null;
        }

        Article article = new Article();
        BeanUtils.copyProperties(articleAdaptorBuilder.getData(), article);
        return article;
    }

    /**
     * 将Article列表按需转换为ArticleVO列表
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return ArticleVO列表
     */
    @Override
    public List<ArticleVO> adaptorArticlesToArticleVOs(ArticleAdaptorBuilder<List<Article>> articleAdaptorBuilder){
        if(Objects.isNull(articleAdaptorBuilder) || CollectionUtils.isEmpty(articleAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<ArticleVO> articleVOs = Lists.newArrayList();
        articleAdaptorBuilder.getData().forEach(article -> {
            if (Objects.isNull(article)){
                return;
            }

            articleVOs.add(adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                    .setCategoryListStr(articleAdaptorBuilder.getCategoryListStr())
                    .setTagList(articleAdaptorBuilder.getTagList())
                    .setRecommend(articleAdaptorBuilder.getRecommend())
                    .setTop(articleAdaptorBuilder.getTop())
                    .setAuthor(articleAdaptorBuilder.getAuthor())
                    .build(article)));
        });

        return articleVOs;
    }


    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeArticleInfoVO getHomeArticleInfoVO() {
        return articleMapperService.getHomeArticleInfoVO();
    }

    /**
     * 分页查询文章列表
     *
     * @param articleVOsQueryDTO articleVOQueryDTO
     * @return 文章列表
     */
    @Override
    public PageUtils queryPage(ArticleVOsQueryDTO articleVOsQueryDTO) {
        if (Objects.isNull(articleVOsQueryDTO) || Objects.isNull(articleVOsQueryDTO.getPage()) ||
                Objects.isNull(articleVOsQueryDTO.getLimit()) || Objects.isNull(articleVOsQueryDTO.getArticleBuilder())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "articleVOQueryDTO，page，limit不能为空");
        }

        if (StringUtils.isBlank(articleVOsQueryDTO.getLogStr())){
            articleVOsQueryDTO.setLogStr("act=queryPageV2 params=" + articleVOsQueryDTO);
        }else {
            articleVOsQueryDTO.setLogStr(articleVOsQueryDTO.getLogStr() + " act=queryPageV2 params=" + articleVOsQueryDTO);
        }

        ArticleQueryContextInfo<ArticleVOsQueryDTO> context = ArticleQueryContextInfo.create(articleVOsQueryDTO);
        context.setPage(articleVOsQueryDTO.getPage());
        context.setLimit(articleVOsQueryDTO.getLimit());
        context.setTitle(articleVOsQueryDTO.getTitle());
        context.setArticleBuilder(articleVOsQueryDTO.getArticleBuilder());

        articleEngine.execute(ArticleVOsQueryFlow.getArticleVOsQueryFlow(), context);
        return new PageUtils(context.getArticleVOIPage());
    }

    /**
     * 保存文章
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleVO articleVO) {
        Article article = adaptorArticleVOToArticle(new ArticleAdaptorBuilder.Builder<ArticleVO>()
                .build(articleVO));
        articleMapperService.saveArticle(article);

        articleVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(articleVO.getId());
            tagLink.setTagId(item.getId());
            tagLink.setModule(ModuleTypeConstants.ARTICLE);
            tagLinkMapperService.save(tagLink);
        });

        // 当文章是发布状态时，需要新增到es中
        if (articleVO.getPublish()){
            InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
            initGitalkRequest.setId(articleVO.getId());
            initGitalkRequest.setTitle(articleVO.getTitle());
            initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
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
            Article article = articleMapperService.getArticle(articleId, null);
            if (Objects.isNull(article)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
            }
            if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者删除");
            }

            //先删除博文标签多对多关联
            tagLinkMapperService.deleteTagLink(articleId, ModuleTypeConstants.ARTICLE);
            articleMapperService.deleteArticles(Arrays.asList(articleId));

            recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleId), ModuleTypeConstants.ARTICLE);
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
        Article article = articleMapperService.getArticle(articleVO.getId(), null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        // 删除多对多所属标签
        tagLinkMapperService.deleteTagLink(articleVO.getId(), ModuleTypeConstants.ARTICLE);
        // 更新所属标签
        articleVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(articleVO.getId());
            tagLink.setTagId(item.getId());
            tagLink.setModule(ModuleTypeConstants.ARTICLE);
            tagLinkMapperService.save(tagLink);
        });

        // 更新
        articleMapperService.updateArticleById(adaptorArticleVOToArticle(new ArticleAdaptorBuilder.Builder<ArticleVO>().build(articleVO)));

        if (!Objects.isNull(articleVO.getRecommend())){
            if (articleVO.getRecommend()){
                if (recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) == null){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null){
                    recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleVO.getId()), ModuleTypeConstants.ARTICLE);
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
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
        }else if (article.getPublish() && !articleVO.getPublish()){
            Integer[] ids = {articleVO.getId()};
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
        }else if (!article.getPublish() && articleVO.getPublish()){
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
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
        Article article = articleMapperService.getArticle(articleVO.getId(), null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        if (!Objects.isNull(articleVO.getPublish())){
            // 更新发布，公开状态
            articleMapperService.updateArticleById(adaptorArticleVOToArticle(new ArticleAdaptorBuilder.Builder<ArticleVO>().build(articleVO)));

            if (article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
            }else if (article.getPublish() && !articleVO.getPublish()){
                Integer[] ids = {articleVO.getId()};
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
            }else if (!article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
            }
        }else if (articleVO.getOpen() != null){
            // 更新公开状态
            articleMapperService.updateArticleById(adaptorArticleVOToArticle(new ArticleAdaptorBuilder.Builder<ArticleVO>()
                    .build(articleVO)));
            if (article.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.getArticle(articleVO.getId(), Article.PUBLISH_TRUE)));
            }
        }

        if (!Objects.isNull(articleVO.getRecommend())){
            // 更新推荐状态
            if (articleVO.getRecommend()){
                if (recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) == null){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null){
                    recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(articleVO.getId()), ModuleTypeConstants.ARTICLE);
                }
            }
        }

        cleanArticlesCache(new Integer[]{articleVO.getId()});
    }

    /**
     * 根据文章id获取文章信息
     *
     * @param articleId 文章id
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @param publish publish
     * @return 文章信息
     */
    @Override
    public ArticleVO getArticleVO(Integer articleId, Boolean publish, ArticleAdaptorBuilder<Article> articleAdaptorBuilder) {
        Article article = articleMapperService.getArticle(articleId, publish);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "后台查看未公开的文章只能由创建者查看");
        }

        return adaptorArticleToArticleVO(articleAdaptorBuilder.setData(article));
    }

    /**
     * 查看未公开文章时检测密码是否正确
     *
     * @param articleId 文章id
     * @param password 密码
     */
    @Override
    public void checkPassword(Integer articleId, String password) {
        Article article = articleMapperService.getArticle(articleId, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
            if (ObjectUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserDTO sysUserDTO = sysUserMapperService.getSysUserDTOByUserId(article.getCreaterId());
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
        return articleMapperService.checkByCategoryId(categoryId);
    }

    /**
     * 判断上传文件下是否有文章
     *
     * @param url url
     * @return 是否有文章
     */
    @Override
    public boolean checkByFile(String url) {
        return articleMapperService.checkByFile(url);
    }

    /**
     * 查询所有已发布的文章
     *
     * @return 所有已发布的文章
     */
    @Override
    public List<Article> listArticlesByPublish() {
        return articleMapperService.listArticlesByPublish();
    }

    /**
     * 根据标题查询所有已发布的文章
     *
     * @param title 标题
     * @return 所有已发布的文章
     */
    @Override
    public List<Article> listArticlesByPublishAndTitle(String title) {
        return articleMapperService.listArticlesByPublishAndTitle(title);
    }

    /**
     * 清除缓存
     *
     * @param ids ids
     * */
    private void cleanArticlesCache(Integer[] ids){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
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
    public PageUtils listArticleVOs(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean read) {
        IPage<Article> articleIPage = articleMapperService.listArticles(page, limit, latest, categoryId, like, read);

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageUtils(articleIPage);
        }

        List<ArticleVO> articleVOs = adaptorArticlesToArticleVOs(new ArticleAdaptorBuilder.Builder<List<Article>>()
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
        List<Integer> linkIds = recommendMapperService.selectLinkIdsByModule(ModuleTypeConstants.ARTICLE);

        IPage<Article> articleIPage = articleMapperService.listHomeArticles(page, limit, linkIds);

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageUtils(articleIPage);
        }

        List<Top> tops = topMapperService.listTops(ModuleTypeConstants.ARTICLE);
        ArticleVO[] articleVOs = new ArticleVO[articleIPage.getRecords().size()];
        if (!CollectionUtils.isEmpty(tops)){
            tops.forEach(topsItem -> {
                if (topsItem.getOrderNum() > (page - 1) * limit && topsItem.getOrderNum() < page * limit){
                    Article article = baseMapper.selectOne(new LambdaUpdateWrapper<Article>()
                            .eq(Article::getId, topsItem.getLinkId())
                            .eq(Article::getPublish, Article.PUBLISH_TRUE));

                    if (Objects.isNull(article)){
                        return;
                    }

                    ArticleVO articleVO = adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                            .setTagList()
                            .setAuthor()
                            .build(article));
                    articleVO.setTop(Article.TOP_TRUE);
                    articleVOs[(topsItem.getOrderNum() - (page - 1) * limit) -1] = articleVO;
                }
            });

            Queue<Article> articleQueue = new LinkedList<>(articleIPage.getRecords());
            for (int i = 0; i < articleVOs.length; i++) {
                if (Objects.isNull(articleVOs[i])){
                    articleVOs[i] = adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
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
        Article article = articleMapperService.getArticle(id, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
            if (ObjectUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserDTO sysUserDTO = sysUserMapperService.getSysUserDTOByUserId(article.getCreaterId());
            if (Objects.isNull(sysUserDTO)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserDTO.getPassword().equals(new Sha256Hash(password, sysUserDTO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }

        ArticleVO articleVO = adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
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
        List<Article> articles = articleMapperService.listHotReadArticles();

        return adaptorArticlesToArticleVOs(new ArticleAdaptorBuilder.Builder<List<Article>>()
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
        Article article = articleMapperService.getArticle(articleId, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        return article.getOpen();
    }

}
