package com.jinhx.blog.service.article;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleAdaptorBuilder;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.dto.PortalArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.base.PageData;

import java.util.List;

/**
 * ArticleService
 *
 * @author jinhx
 * @since 2018-11-21
 */
public interface ArticleService extends IService<Article> {

    /**
     * 将Article按需转换为ArticleVO
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return ArticleVO
     */
    ArticleVO adaptorArticleToArticleVO(ArticleAdaptorBuilder<Article> articleAdaptorBuilder);

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeArticleInfoVO getHomeArticleInfoVO();

    /**
     * 分页查询文章列表
     *
     * @param articleVOIPageQueryDTO articleVOQueryDTO
     * @return 文章列表
     */
    PageData queryPage(ArticleVOIPageQueryDTO articleVOIPageQueryDTO);

    /**
     * 保存文章
     *
     * @param articleVO 文章信息
     */
    void saveArticle(ArticleVO articleVO);

    /**
     * 批量删除
     *
     * @param ids 文章id列表
     */
    void deleteArticles(Integer[] ids);

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     */
    void updateArticle(ArticleVO articleVO);

    /**
     * 更新文章状态
     *
     * @param articleVO 文章信息
     */
    void updateArticleStatus(ArticleVO articleVO);

    /**
     * 根据文章id获取文章信息
     *
     * @param articleVOsQueryDTO articleVOsQueryDTO
     * @return 文章信息
     */
    List<ArticleVO> getArticleVOs(ArticleVOsQueryDTO articleVOsQueryDTO);

    /**
     * 查看未公开文章时检测密码是否正确
     *
     * @param articleId 文章id
     * @param password 密码
     */
    void checkPassword(Integer articleId, String password);

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
     * @param portalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO
     * @return 文章列表
     */
    PageData listArticleVOs(PortalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO);

    /**
     * 分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    PageData listHomeArticles(Integer page, Integer limit);

    /**
     * 获取ArticleVO对象
     *
     * @param id id
     * @param password password
     * @return ArticleVO
     */
    ArticleVO getArticleVOByPassword(Integer id, String password);

    /**
     * 获取热读榜
     *
     * @param baseRequestDTO baseRequestDTO
     * @param articleBuilder articleBuilder
     * @return 热读文章列表
     */
    List<ArticleVO> listHotReadArticles(BaseRequestDTO baseRequestDTO, ArticleBuilder articleBuilder);

    /**
     * 文章点赞
     *
     * @param id id
     * @return 点赞结果
     */
    Boolean updateArticle(Integer id) throws Exception;

    /**
     * 根据文章id获取公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    Boolean getArticleOpenById(Integer articleId);

}
