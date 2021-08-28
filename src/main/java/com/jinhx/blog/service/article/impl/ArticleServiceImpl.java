package com.jinhx.blog.service.article.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.SelectGroup;
import com.jinhx.blog.engine.article.ArticleEngine;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.engine.article.flow.SelectArticleFlow;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleAdaptorBuilder;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.dto.PortalArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.base.LogicExecutor;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.sys.vo.SysUserVO;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.*;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.service.sys.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * ArticleServiceImpl
 *
 * @author jinhx
 * @since 2018-11-21
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

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
    private SysUserService sysUserService;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private CategoryService categoryService;

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
    public ArticleVO adaptorArticleToArticleVO(ArticleAdaptorBuilder<Article> articleAdaptorBuilder){
        if(Objects.isNull(articleAdaptorBuilder) || Objects.isNull(articleAdaptorBuilder.getData())){
            return null;
        }

        Article article = articleAdaptorBuilder.getData();
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);

        if (articleAdaptorBuilder.getCategoryListStr()){
            articleVO.setCategoryListStr(categoryService.adaptorcategoryIdsToCategoryNames(articleVO.getCategoryId(), ModuleTypeConstants.ARTICLE));
        }

        if (articleAdaptorBuilder.getTagList()){
            List<TagLink> tagLinks = tagLinkMapperService.selectTagLinksByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE);
            if (CollectionUtils.isNotEmpty(tagLinks)){
                articleVO.setTagList(tagMapperService.listByLinkId(tagLinks));
            }
        }

        if (articleAdaptorBuilder.getRecommend()){
            articleVO.setRecommend(Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE)));
        }

        if (articleAdaptorBuilder.getTop()){
            articleVO.setTop(topMapperService.selectTopCountByOrderNum(ModuleTypeConstants.ARTICLE, articleVO.getArticleId()) > 0);
        }

        if (articleAdaptorBuilder.getAuthor()){
            articleVO.setAuthor(sysUserMapperService.selectNicknameBySysUserId(articleVO.getCreaterId()));
        }

        return articleVO;
    }

    /**
     * 将ArticleVO转换为Article
     *
     * @param articleVO articleVO
     * @return Article
     */
    private Article adaptorArticleVOToArticle(ArticleVO articleVO) {
        if (Objects.isNull(articleVO)) {
            return null;
        }

        Article article = new Article();
        BeanUtils.copyProperties(articleVO, article);
        return article;
    }

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeArticleInfoVO selectHomeArticleInfoVO() {
        return articleMapperService.selectHomeArticleInfoVO();
    }

    /**
     * 分页查询文章列表
     *
     * @param articleVOIPageQueryDTO articleVOQueryDTO
     * @return 文章列表
     */
    @Override
    public PageData<ArticleVO> selectPage(ArticleVOIPageQueryDTO articleVOIPageQueryDTO) {
        return new LogicExecutor<PageData<ArticleVO>>() {

            ArticleQueryContextInfo<ArticleVOIPageQueryDTO> context;

            @Override
            protected void checkParams() {
                ValidatorUtils.validateEntity(articleVOIPageQueryDTO, SelectGroup.class);

                articleVOIPageQueryDTO.setLogStr("act=selectPage");
                context = ArticleQueryContextInfo.create(articleVOIPageQueryDTO);
            }

            @Override
            protected PageData<ArticleVO> process() {
                context.setPage(articleVOIPageQueryDTO.getPage());
                context.setLimit(articleVOIPageQueryDTO.getLimit());
                context.setTitle(articleVOIPageQueryDTO.getTitle());
                context.setArticleBuilder(articleVOIPageQueryDTO.getArticleBuilder());

                articleEngine.execute(SelectArticleFlow.getSelectArticleVOPageFlow(), context);
                return new PageData<>(context.getArticleVOIPage());
            }

            @Override
            protected String getParams() {
                if (Objects.isNull(articleVOIPageQueryDTO)){
                    return null;
                }
                return JsonUtils.objectToJson(articleVOIPageQueryDTO);
            }

            @Override
            protected String getProcessorName() {
                return "selectPage";
            }

        }.execute();
    }

    /**
     * 根据条件查询文章列表
     *
     * @param articleVOsQueryDTO articleVOsQueryDTO
     * @return 文章列表
     */
    @Override
    public List<ArticleVO> selectArticleVOs(ArticleVOsQueryDTO articleVOsQueryDTO) {
        return new LogicExecutor<List<ArticleVO>>() {

            ArticleQueryContextInfo<ArticleVOsQueryDTO> context;

            @Override
            protected void checkParams() {
                ValidatorUtils.validateEntity(articleVOsQueryDTO, SelectGroup.class);

                articleVOsQueryDTO.setLogStr("act=selectArticleVOs");
                context = ArticleQueryContextInfo.create(articleVOsQueryDTO);
            }

            @Override
            protected List<ArticleVO> process() {
                context.setPublish(articleVOsQueryDTO.getPublish());
                context.setArticleIds(articleVOsQueryDTO.getArticleIds());
                context.setArticleBuilder(articleVOsQueryDTO.getArticleBuilder());

                articleEngine.execute(SelectArticleFlow.getSelectArticleVOsFlow(), context);

                if (CollectionUtils.isEmpty(context.getArticles())){
                    return Lists.newArrayList();
                }
                return context.getArticleVOs();
            }

            @Override
            protected String getParams() {
                if (Objects.isNull(articleVOsQueryDTO)){
                    return null;
                }
                return JsonUtils.objectToJson(articleVOsQueryDTO);
            }

            @Override
            protected String getProcessorName() {
                return "selectArticleVOs";
            }

        }.execute();
    }

    /**
     * 新增文章
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertArticleVO(ArticleVO articleVO) {
        Article article = adaptorArticleVOToArticle(articleVO);
        articleMapperService.insertArticle(article);

        articleVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(articleVO.getArticleId());
            tagLink.setTagId(item.getTagId());
            tagLink.setModule(ModuleTypeConstants.ARTICLE);
            tagLinkMapperService.insertTagLink(tagLink);
        });

        // 当文章是发布状态时，需要新增到es中
        if (articleVO.getPublish()){
            InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
            initGitalkRequest.setId(articleVO.getArticleId());
            initGitalkRequest.setTitle(articleVO.getTitle());
            initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
        }

        cleanArticlesCache(Lists.newArrayList());
    }

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleVO(ArticleVO articleVO) {
        Article article = articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!article.getCreaterId().equals(SysAdminUtils.getSysUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        // 删除多对多所属标签
        tagLinkMapperService.deleteTagLinksByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE);
        // 更新所属标签
        articleVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(articleVO.getArticleId());
            tagLink.setTagId(item.getTagId());
            tagLink.setModule(ModuleTypeConstants.ARTICLE);
            tagLinkMapperService.insertTagLink(tagLink);
        });

        // 更新
        articleMapperService.updateArticleById(articleVO);

        if (Objects.nonNull(articleVO.getRecommend())){
            if (articleVO.getRecommend()){
                if (Objects.isNull(recommendMapperService.selectRecommendByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE))){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getArticleId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE))){
                    recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(articleVO.getArticleId()), ModuleTypeConstants.ARTICLE);
                }
            }
        }
        // 发送rabbitmq消息同步到es
        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(articleVO.getArticleId());
        initGitalkRequest.setTitle(articleVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));

        if (article.getPublish() && articleVO.getPublish()){
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
        }else if (article.getPublish() && !articleVO.getPublish()){
            List<Long> articleIds = Lists.newArrayList(articleVO.getArticleId());
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(articleIds));
        }else if (!article.getPublish() && articleVO.getPublish()){
            rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                    JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
        }

        cleanArticlesCache(Lists.newArrayList(articleVO.getArticleId()));
    }

    /**
     * 批量删除文章
     *
     * @param articleIds 文章id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticlesById(List<Long> articleIds) {
        articleIds.forEach(articleId -> {
            Article article = articleMapperService.selectArticleByIdAndPublish(articleId, null);
            if (Objects.isNull(article)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
            }
            if (!article.getCreaterId().equals(SysAdminUtils.getSysUserId()) && !article.getOpen()){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者删除");
            }

            //先删除博文标签多对多关联
            tagLinkMapperService.deleteTagLinksByLinkIdAndModule(articleId, ModuleTypeConstants.ARTICLE);
            articleMapperService.deleteArticleById(articleId);

            recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(articleId), ModuleTypeConstants.ARTICLE);
        });

        // 发送rabbitmq消息同步到es
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(articleIds));

        cleanArticlesCache(articleIds);
    }

    /**
     * 更新文章状态
     *
     * @param articleVO 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleStatus(ArticleVO articleVO) {
        Article article = articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }
        if (!article.getCreaterId().equals(SysAdminUtils.getSysUserId()) && !article.getOpen()){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "未公开的文章只能由创建者修改");
        }

        if (Objects.nonNull(articleVO.getPublish())){
            // 更新发布，公开状态
            articleMapperService.updateArticleById(articleVO);

            if (article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
            }else if (article.getPublish() && !articleVO.getPublish()){
                List<Long> articleIds = Lists.newArrayList(articleVO.getArticleId());
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_DELETE_ROUTINGKEY, JsonUtils.objectToJson(articleIds));
            }else if (!article.getPublish() && articleVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
            }
        }else if (Objects.nonNull(articleVO.getOpen())){
            // 更新公开状态
            articleMapperService.updateArticleById(articleVO);
            if (article.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_UPDATE_ROUTINGKEY,
                        JsonUtils.objectToJson(articleMapperService.selectArticleByIdAndPublish(articleVO.getArticleId(), Article.PublishEnum.YES.getCode())));
            }
        }

        if (Objects.nonNull(articleVO.getRecommend())){
            // 更新推荐状态
            if (articleVO.getRecommend()){
                if (Objects.isNull(recommendMapperService.selectRecommendByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE))){
                    int maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.ARTICLE);
                    recommend.setLinkId(articleVO.getArticleId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(articleVO.getArticleId(), ModuleTypeConstants.ARTICLE))){
                    recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(articleVO.getArticleId()), ModuleTypeConstants.ARTICLE);
                }
            }
        }

        cleanArticlesCache(Lists.newArrayList(articleVO.getArticleId()));
    }

    /**
     * 查看未公开文章时检测密码是否正确
     *
     * @param articleId 文章id
     * @param password 密码
     */
    @Override
    public void checkPassword(Long articleId, String password) {
        Article article = articleMapperService.selectArticleByIdAndPublish(articleId, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!article.getCreaterId().equals(SysAdminUtils.getSysUserId()) && !article.getOpen()){
            if (ObjectUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserVO sysUserVO = sysUserService.selectSysUserVOById(article.getCreaterId());
            if (sysUserVO == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserVO.getPassword().equals(new Sha256Hash(password, sysUserVO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param articleIds articleIds
     * */
    private void cleanArticlesCache(List<Long> articleIds){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanArticlesCache(articleIds);
        });
    }

    /********************** portal ********************************/

    /**
     * 分页查询文章列表
     *
     * @param portalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO
     * @return 文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES)
    @Override
    public PageData<ArticleVO> selectPortalPage(PortalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO) {
        return new LogicExecutor<PageData<ArticleVO>>() {

            ArticleQueryContextInfo<PortalArticleVOIPageQueryDTO> context;

            @Override
            protected void checkParams() {
                ValidatorUtils.validateEntity(portalArticleVOIPageQueryDTO, SelectGroup.class);

                portalArticleVOIPageQueryDTO.setLogStr("act=selectPortalPage");
                context = ArticleQueryContextInfo.create(portalArticleVOIPageQueryDTO);
            }

            @Override
            protected PageData<ArticleVO> process() {
                context.setPage(portalArticleVOIPageQueryDTO.getPage());
                context.setLimit(portalArticleVOIPageQueryDTO.getLimit());
                context.setCategoryId(portalArticleVOIPageQueryDTO.getCategoryId());
                context.setLatest(portalArticleVOIPageQueryDTO.getLatest());
                context.setLike(portalArticleVOIPageQueryDTO.getLike());
                context.setRead(portalArticleVOIPageQueryDTO.getRead());
                context.setArticleBuilder(portalArticleVOIPageQueryDTO.getArticleBuilder());

                articleEngine.execute(SelectArticleFlow.getSelectPortalArticleVOPageFlow(), context);
                return new PageData<>(context.getArticleVOIPage());
            }

            @Override
            protected String getParams() {
                if (Objects.isNull(portalArticleVOIPageQueryDTO)){
                    return null;
                }
                return JsonUtils.objectToJson(portalArticleVOIPageQueryDTO);
            }

            @Override
            protected String getProcessorName() {
                return "selectPortalPage";
            }

        }.execute();
    }

    /**
     * 分页查询首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES)
    @Override
    public PageData<ArticleVO> selectPortalHomePage(Integer page, Integer limit) {
        List<Long> linkIds = recommendMapperService.selectLinkIdsByModule(ModuleTypeConstants.ARTICLE);

        IPage<Article> articleIPage = articleMapperService.selectPortalHomePage(page, limit, linkIds);

        if (CollectionUtils.isEmpty(articleIPage.getRecords())){
            return new PageData<>();
        }

        List<Top> tops = topMapperService.selectPortalTopsByModule(ModuleTypeConstants.ARTICLE);
        ArticleVO[] articleVOs = new ArticleVO[articleIPage.getRecords().size()];
        if (CollectionUtils.isNotEmpty(tops)){
            tops.forEach(topsItem -> {
                if (topsItem.getOrderNum() > (page - 1) * limit && topsItem.getOrderNum() < page * limit){
                    Article article = articleMapperService.selectArticleByIdAndPublish(topsItem.getLinkId(), Article.PublishEnum.YES.getCode());

                    if (Objects.isNull(article)){
                        return;
                    }

                    ArticleVO articleVO = adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                            .setTagList()
                            .setAuthor()
                            .build(article));
                    articleVO.setTop(ArticleVO.TopEnum.YSE.getCode());
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
        articleVOIPage.setRecords(Lists.newArrayList(articleVOs));
        return new PageData<>(articleVOIPage);
    }

    /**
     * 查询ArticleVO对象
     *
     * @param articleId articleId
     * @param password password
     * @return ArticleVO
     */
    @Cacheable(value = RedisKeyConstants.ARTICLE, key = "#articleId + ':' + #password")
    @Override
    public ArticleVO selectArticleVOByPassword(Long articleId, String password) {
        Article article = articleMapperService.selectArticleByIdAndPublish(articleId, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        if (!article.getCreaterId().equals(SysAdminUtils.getSysUserId()) && !article.getOpen()){
            if (ObjectUtils.isEmpty(password)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请输入密码");
            }
            SysUserVO sysUserVO = sysUserService.selectSysUserVOById(article.getCreaterId());
            if (Objects.isNull(sysUserVO)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章创建者不存在");
            }

            if (!sysUserVO.getPassword().equals(new Sha256Hash(password, sysUserVO.getSalt()).toHex())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "密码错误");
            }
        }

        ArticleVO articleVO = adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                .setTagList()
                .setAuthor()
                .build(article));

        // 浏览数量
        articleMapperService.addArticleReadNum(articleId);
        return articleVO;
    }

    /**
     * 查询热读文章列表
     *
     * @param baseRequestDTO baseRequestDTO
     * @param articleBuilder articleBuilder
     * @return 热读文章列表
     */
    @Cacheable(value = RedisKeyConstants.ARTICLES, key = "'hostread'")
    @Override
    public List<ArticleVO> selectHotReadArticleVOs(BaseRequestDTO baseRequestDTO, ArticleBuilder articleBuilder) {
        return new LogicExecutor<List<ArticleVO>>() {

            ArticleQueryContextInfo<BaseRequestDTO> context;

            @Override
            protected void checkParams() {
                MyAssert.notNull(baseRequestDTO, "baseRequestDTO不能为空");
                MyAssert.notNull(articleBuilder, "articleBuilder不能为空");

                baseRequestDTO.setLogStr("act=selectHotReadArticleVOs");
                context = ArticleQueryContextInfo.create(baseRequestDTO);
            }

            @Override
            protected List<ArticleVO> process() {
                context.setArticleBuilder(articleBuilder);

                articleEngine.execute(SelectArticleFlow.getSelectHotReadArticleVOsFlow(), context);
                return context.getArticleVOs();
            }

            @Override
            protected String getParams() {
                if (Objects.isNull(baseRequestDTO)){
                    return null;
                }
                return JsonUtils.objectToJson(baseRequestDTO);
            }

            @Override
            protected String getProcessorName() {
                return "selectHotReadArticleVOs";
            }

        }.execute();
    }

    /**
     * 文章点赞
     *
     * @param articleId articleId
     */
    @Override
    public void addArticleLikeNum(Long articleId) throws Exception {
        //获取request
        ParamsHttpServletRequestWrapper request = (ParamsHttpServletRequestWrapper) HttpContextUtils.getHttpServletRequest();
        String userId = EncodeUtils.encoderByMD5(IPUtils.getIpAddr(request) + UserAgentUtils.getBrowserName(request) +
                UserAgentUtils.getBrowserVersion(request) + UserAgentUtils.getDeviceManufacturer(request) +
                UserAgentUtils.getDeviceType(request) + UserAgentUtils.getOsVersion(request));
        // 每天重新计算点赞
        if (!RedisUtils.setIfAbsent(BLOG_ARTICLE_LIKE_LOCK_KEY + userId + ":" + articleId, "1", DateUtils.getRemainMilliSecondsOneDay())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "1天只能点赞1次，请明天再来点赞");
        }

        articleMapperService.addArticleLikeNum(articleId);
    }

    /**
     * 根据文章id查询公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    @Override
    public Boolean selectArticleOpenById(Long articleId) {
        Article article = articleMapperService.selectArticleByIdAndPublish(articleId, null);
        if (Objects.isNull(article)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文章不存在");
        }

        return article.getOpen();
    }

}
