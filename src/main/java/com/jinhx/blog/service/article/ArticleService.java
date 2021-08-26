package com.jinhx.blog.service.article;

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
public interface ArticleService {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeArticleInfoVO selectHomeArticleInfoVO();

    /**
     * 分页查询文章列表
     *
     * @param articleVOIPageQueryDTO articleVOQueryDTO
     * @return 文章列表
     */
    PageData<ArticleVO> selectPage(ArticleVOIPageQueryDTO articleVOIPageQueryDTO);

    /**
     * 根据条件查询文章列表
     *
     * @param articleVOsQueryDTO articleVOsQueryDTO
     * @return 文章列表
     */
    List<ArticleVO> selectArticleVOs(ArticleVOsQueryDTO articleVOsQueryDTO);

    /**
     * 新增文章
     *
     * @param articleVO 文章信息
     */
    void insertArticleVO(ArticleVO articleVO);

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     */
    void updateArticleVO(ArticleVO articleVO);

    /**
     * 批量删除文章
     *
     * @param articleIds 文章id列表
     */
    void deleteArticlesById(List<Long> articleIds);

    /**
     * 更新文章状态
     *
     * @param articleVO 文章信息
     */
    void updateArticleStatus(ArticleVO articleVO);

    /**
     * 查看未公开文章时检测密码是否正确
     *
     * @param articleId 文章id
     * @param password 密码
     */
    void checkPassword(Long articleId, String password);

    /********************** portal ********************************/

    /**
     * 分页查询文章列表
     *
     * @param portalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO
     * @return 文章列表
     */
    PageData<ArticleVO> selectPortalPage(PortalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO);

    /**
     * 分页查询首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    PageData<ArticleVO> selectPortalHomePage(Integer page, Integer limit);

    /**
     * 查询ArticleVO对象
     *
     * @param articleId articleId
     * @param password password
     * @return ArticleVO
     */
    ArticleVO selectArticleVOByPassword(Long articleId, String password);

    /**
     * 查询热读文章列表
     *
     * @param baseRequestDTO baseRequestDTO
     * @param articleBuilder articleBuilder
     * @return 热读文章列表
     */
    List<ArticleVO> selectHotReadArticleVOs(BaseRequestDTO baseRequestDTO, ArticleBuilder articleBuilder);

    /**
     * 文章点赞
     *
     * @param articleId articleId
     */
    void addArticleLikeNum(Long articleId) throws Exception;

    /**
     * 根据文章id查询公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    Boolean selectArticleOpenById(Long articleId);

}
