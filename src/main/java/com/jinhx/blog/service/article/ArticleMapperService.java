package com.jinhx.blog.service.article;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.mapper.article.ArticleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * ArticleMapperService
 *
 * @author jinhx
 * @since 2018-11-21
 */
@Service
public class ArticleMapperService extends ServiceImpl<ArticleMapper, Article> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeArticleInfoVO selectHomeArticleInfoVO() {
        Integer publishCount = baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PublishEnum.YES.getCode()));
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
    public IPage<Article> selectPage(Integer page, Integer limit, String title) {
        return baseMapper.selectPage(new QueryPage<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .like(ObjectUtil.isNotEmpty(title), Article::getTitle, title)
                .orderByDesc(Article::getUpdateTime));
    }

    /**
     * 新增文章
     *
     * @param article 文章
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertArticle(Article article) {
        insertArticles(Lists.newArrayList(article));
    }

    /**
     * 批量新增文章
     *
     * @param articles 文章列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertArticles(List<Article> articles) {
        if (CollectionUtils.isNotEmpty(articles)){
            if (articles.stream().mapToInt(item -> baseMapper.insert(item)).sum() != articles.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据articleId删除文章
     *
     * @param articleId 文章id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleById(Long articleId) {
        deleteArticlesById(Lists.newArrayList(articleId));
    }

    /**
     * 批量根据articleId删除文章
     *
     * @param articleIds 文章id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticlesById(List<Long> articleIds) {
        if (CollectionUtils.isNotEmpty(articleIds)){
            if (baseMapper.deleteBatchIds(articleIds) != articleIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 根据articleId更新文章
     *
     * @param article 文章
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleById(Article article) {
        updateArticlesById(Lists.newArrayList(article));
    }

    /**
     * 批量根据articleId更新文章
     *
     * @param articles 文章列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArticlesById(List<Article> articles) {
        if (CollectionUtils.isNotEmpty(articles)){
            if (articles.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != articles.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 根据文章id，发布状态查询文章
     *
     * @param articleId 文章id
     * @param publish publish
     * @return 文章
     */
    public Article selectArticleByIdAndPublish(Long articleId, Boolean publish) {
        List<Article> articles = selectArticlesByIdAndPublish(Lists.newArrayList(articleId), publish);
        if (CollectionUtils.isEmpty(articles)){
            return null;
        }

        return articles.get(0);
    }

    /**
     * 根据文章id列表，发布状态查询文章列表
     *
     * @param articleIds 文章id列表
     * @param publish publish
     * @return 文章列表
     */
    public List<Article> selectArticlesByIdAndPublish(List<Long> articleIds, Boolean publish) {
        if (CollectionUtils.isEmpty(articleIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .in(CollectionUtils.isNotEmpty(articleIds), Article::getArticleId, articleIds)
                .eq(Objects.nonNull(publish), Article::getPublish, publish));
    }

    /**
     * 查询类别下是否有文章
     *
     * @param categoryId categoryId
     * @return 是否有文章
     */
    public boolean existByCategoryId(Long categoryId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .like(Objects.nonNull(categoryId), Article::getCategoryId, categoryId)) > 0;
    }

    /**
     * 查询上传文件是否有文章封面占用
     *
     * @param url url
     * @return 是否有文章封面占用
     */
    public boolean existByCover(String url) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(ObjectUtil.isNotEmpty(url), Article::getCover, url)) > 0;
    }

    /**
     * 根据发布状态查询文章列表
     *
     * @param publish publish
     * @return 文章列表
     */
    public List<Article> selectArticlesByPublish(Boolean publish) {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Objects.nonNull(publish), Article::getPublish, publish));
    }

    /**
     * 根据标题，发布状态查询文章列表
     *
     * @param title 标题
     * @param publish publish
     * @return 文章列表
     */
    public List<Article> selectArticlesByTitleAndPublish(String title, Boolean publish) {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Objects.nonNull(publish), Article::getPublish, publish)
                .like(StringUtils.isNotEmpty(title), Article::getTitle, title)
                .orderByDesc(Article::getArticleId));
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
    public IPage<Article> selectPortalPage(Integer page, Integer limit, Long categoryId, Boolean latest, Boolean like, Boolean read) {
        return baseMapper.selectPage(new QueryPage<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PublishEnum.YES.getCode())
                .like(Objects.nonNull(categoryId), Article::getCategoryId, categoryId)
                .orderByDesc(latest, Article::getCreateTime)
                .orderByDesc(like, Article::getLikeNum)
                .orderByDesc(read, Article::getReadNum)
                .select(Article::getArticleId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));
    }

    /**
     * 根据articleId分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param articleIds articleIds
     * @return 首页文章列表
     */
    public IPage<Article> selectPortalHomePage(Integer page, Integer limit, List<Long> articleIds) {
        return baseMapper.selectPage(new QueryPage<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PublishEnum.YES.getCode())
                .notIn(CollectionUtils.isNotEmpty(articleIds), Article::getArticleId, articleIds)
                .orderByDesc(Article::getCreateTime)
                .select(Article::getArticleId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));
    }

    /**
     * 文章阅读
     *
     * @param articleId articleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addArticleReadNum(Long articleId) {
        if (baseMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getArticleId, articleId)
                .setSql("read_num = read_num + 1")) != 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 文章点赞
     *
     * @param articleId articleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addArticleLikeNum(Long articleId) {
        if (baseMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getArticleId, articleId)
                .setSql("like_num = like_num + 1")) != 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 查询热读文章列表
     *
     * @return 热读文章列表
     */
    public List<Article> selectHotReadArticles() {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PublishEnum.YES.getCode())
                .eq(Article::getOpen, Article.OpenEnum.YES.getCode())
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 5")
                .orderByDesc(Article::getReadNum)
                .select(Article::getArticleId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));
    }

}
