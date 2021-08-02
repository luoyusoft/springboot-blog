package com.jinhx.blog.service.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.mapper.article.ArticleMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * ArticleServiceImpl
 *
 * @author jinhx
 * @since 2018-11-21
 */
@Service
public class ArticleMapperServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleMapperService {

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
    public IPage<Article> queryPage(Integer page, Integer limit, String title) {
        return baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(ObjectUtil.isNotEmpty(title), Article::getTitle, title)
                .orderByDesc(Article::getUpdateTime)
                .select(Article::getId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));
    }

    /**
     * 保存文章
     *
     * @param article 文章信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(Article article) {
        baseMapper.insert(article);
    }

    /**
     * 批量删除
     *
     * @param ids 文章id列表
     */
    @Override
    public void deleteArticles(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 更新文章
     *
     * @param article 文章信息
     */
    @Override
    public void updateArticleById(Article article) {
        baseMapper.updateById(article);
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
    @Override
    public IPage<Article> listArticles(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean read) {
        return baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .like(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(latest, Article::getCreateTime)
                .orderByDesc(like, Article::getLikeNum)
                .orderByDesc(read, Article::getReadNum));
    }

    /**
     * 分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @Override
    public IPage<Article> listHomeArticles(Integer page, Integer limit, List<Integer> linkIds) {
        return baseMapper.selectPage(new Query<Article>(page, limit).getPage(), new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .notIn(!CollectionUtils.isEmpty(linkIds), Article::getId)
                .orderByDesc(Article::getCreateTime));
    }

    /**
     * 获取热读榜
     *
     * @return 热读文章列表
     */
    @Override
    public List<Article> listHotReadArticles() {
        return baseMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getPublish, Article.PUBLISH_TRUE)
                .eq(Article::getOpen, Article.OPEN_TRUE)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 5")
                .orderByDesc(Article::getReadNum)
                .select(Article::getId, Article::getTitle, Article::getDescription, Article::getReadNum, Article::getLikeNum,
                        Article::getCover, Article::getCoverType, Article::getCategoryId, Article::getPublish, Article::getOpen,
                        Article::getCreaterId, Article::getUpdaterId, Article::getCreateTime, Article::getUpdateTime));
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
