package com.jinhx.blog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.dto.ArticleDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ArticleMapper
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取已发布文章数量
     * @return 已发布文章数量
     */
    Integer selectPublishCount();

    /**
     * 获取所有文章数量
     * @return 所有文章数量
     */
    Integer selectAllCount();

    /**
     * 查询列表
     *
     * @param page
     * @param params
     * @return
     */
    List<ArticleDTO> listArticleDTO(Page<ArticleDTO> page, @Param("params") Map<String, Object> params);

    /**
     * 更新阅读记录
     * @param id
     */
    Boolean updateReadNum(Integer id);

    /**
     * 更新点赞记录
     * @param id
     */
    Boolean updateLikeNum(Integer id);

    /**
     * 判断类别下是否有文章
     * @param categoryId
     * @return
     */
    Integer checkByCategory(Integer categoryId);

    /**
     * 判断上传文件下是否有文章
     * @param url
     * @return
     */
    Integer checkByFile(String url);

    /**
     * 查询所有文章列表
     * @return
     */
    List<ArticleDTO> selectArticleDTOList();

    /**
     * 查询所有文章列表
     * @return
     */
    List<Article> selectArticleListByTitle(String title);

    /**
     * 更新文章
     * @return
     */
    Boolean updateArticleById(Article article);

    /********************** portal ********************************/

    /**
     * 根据条件查询分页
     * @param page
     * @param params
     * @return
     */
    List<ArticleDTO> queryPageCondition(Page<ArticleDTO> page, @Param("params") Map<String, Object> params);

    /**
     * 根据条件查询首页分页
     * @param page
     * @param params
     * @return
     */
    List<ArticleDTO> queryHomePageCondition(Page<ArticleVO> page, @Param("params") Map<String, Object> params);

    /**
     * 获取简单的对象
     * @param id
     * @return
     */
    ArticleDTO getSimpleArticleDTO(Integer id);

    /**
     * 获取热读榜
     * @return 文章列表
     */
    List<ArticleDTO> getHotReadList();

    /**
     * 查询已发布文章
     * @return
     */
    Article selectArticleById(Integer id);

}
