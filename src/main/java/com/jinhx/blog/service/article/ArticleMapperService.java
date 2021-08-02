package com.jinhx.blog.service.article;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;

import java.util.List;

/**
 * ArticleService
 *
 * @author jinhx
 * @since 2018-11-21
 */
public interface ArticleMapperService extends IService<Article> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeArticleInfoVO getHomeArticleInfoVO();

    /**
     * 分页查询文章列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 文章列表
     */
    IPage<Article> queryPage(Integer page, Integer limit, String title);

    /**
     * 保存文章
     *
     * @param article 文章信息
     */
    void saveArticle(Article article);

    /**
     * 批量删除
     *
     * @param ids 文章id列表
     */
    void deleteArticles(List<Integer> ids);

    /**
     * 更新文章
     *
     * @param article 文章信息
     */
    void updateArticleById(Article article);

    /**
     * 根据文章id获取文章信息
     *
     * @param articleId 文章id
     * @param publish publish
     * @return 文章信息
     */
    Article getArticle(Integer articleId, Boolean publish);

    /**
     * 判断类别下是否有文章
     *
     * @param categoryId categoryId
     * @return 是否有文章
     */
    boolean checkByCategoryId(Integer categoryId);

    /**
     * 判断上传文件下是否有文章
     * @param url url
     * @return 是否有文章
     */
    boolean checkByFile(String url);

    /**
     * 查询所有已发布的文章
     *
     * @return 所有已发布的文章
     */
    List<Article> listArticlesByPublish();

    /**
     * 根据标题查询所有已发布的文章
     *
     * @param title 标题
     * @return 所有已发布的文章
     */
    List<Article> listArticlesByPublishAndTitle(String title);

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
    IPage<Article> listArticles(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean read);

    /**
     * 分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    IPage<Article> listHomeArticles(Integer page, Integer limit, List<Integer> linkIds);

    /**
     * 获取热读榜
     *
     * @return 热读文章列表
     */
    List<Article> listHotReadArticles();

    /**
     * 根据文章id获取公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    Boolean getArticleOpenById(Integer articleId);

}
