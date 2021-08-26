package com.jinhx.blog.controller.article;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.dto.PortalArticleVOIPageQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.article.vo.HomeArticleInfoVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.service.article.ArticleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ArticleController
 *
 * @author jinhx
 * @since 2018-11-20
 */
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/article/homeinfo")
    @RequiresPermissions("article:list")
    public Response<HomeArticleInfoVO> selectHomeArticleInfoVO() {
        return Response.success(articleService.selectHomeArticleInfoVO());
    }

    /**
     * 分页查询文章列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 文章列表
     */
    @GetMapping("/manage/article/list")
    @RequiresPermissions("article:list")
    public Response<PageData<ArticleVO>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("title") String title) {
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");

        ArticleVOIPageQueryDTO articleVOIPageQueryDTO = new ArticleVOIPageQueryDTO();
        articleVOIPageQueryDTO.setLogStr("con=selectPage");
        articleVOIPageQueryDTO.setPage(page);
        articleVOIPageQueryDTO.setLimit(limit);
        articleVOIPageQueryDTO.setTitle(title);
        articleVOIPageQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                .categoryListStr(true)
                .tagList(true)
                .recommend(true)
                .top(true)
                .author(true)
                .build());

        return Response.success(articleService.selectPage(articleVOIPageQueryDTO));
    }

    /**
     * 根据articleId查询文章信息
     *
     * @param articleId 文章id
     * @return 文章信息
     */
    @GetMapping("/manage/article/info/{articleId}")
    @RequiresPermissions("article:list")
    public Response<ArticleVO> selectArticleVOById(@PathVariable("articleId") Long articleId) {
        MyAssert.notNull(articleId, "articleId不能为空");

        ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
        articleVOsQueryDTO.setLogStr("con=selectArticleVOById");
        articleVOsQueryDTO.setArticleIds(Lists.newArrayList(articleId));
        articleVOsQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                .categoryListStr(true)
                .tagList(true)
                .recommend(true)
                .top(true)
                .author(true)
                .build());

        List<ArticleVO> articleVOs = articleService.selectArticleVOs(articleVOsQueryDTO);
        if (CollectionUtils.isEmpty(articleVOs)){
            return Response.success();
        }

        return Response.success(articleVOs.get(0));
    }

    /**
     * 新增文章
     *
     * @param articleVO 文章信息
     * @return 新增结果
     */
    @PostMapping("/manage/article/save")
    @RequiresPermissions("article:save")
    public Response<Void> insertArticleVO(@RequestBody ArticleVO articleVO){
        ValidatorUtils.validateEntity(articleVO, InsertGroup.class);
        articleService.insertArticleVO(articleVO);
        return Response.success();
    }

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     * @return 更新结果
     */
    @PutMapping("/manage/article/update")
    @RequiresPermissions("article:update")
    public Response<Void> updateArticleVO(@RequestBody ArticleVO articleVO){
        articleService.updateArticleVO(articleVO);
        return Response.success();
    }

    /**
     * 更新文章状态
     *
     * @param articleVO 文章信息
     * @return 更新结果
     */
    @PutMapping("/manage/article/update/status")
    @RequiresPermissions("article:update")
    public Response<Void> updateArticleStatus(@RequestBody ArticleVO articleVO){
        articleService.updateArticleStatus(articleVO);
        return Response.success();
    }

    /**
     * 批量删除文章
     *
     * @param articleIds 文章id列表
     * @return 删除结果
     */
    @DeleteMapping("/manage/article/delete")
    @RequiresPermissions("article:delete")
    public Response<Void> deleteArticlesById(@RequestBody List<Long> articleIds) {
        MyAssert.sizeBetween(articleIds, 1, 100, "articleIds");
        articleService.deleteArticlesById(articleIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 分页查询文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param categoryId 分类
     * @param latest 时间排序
     * @param like 点赞量排序
     * @param read 阅读量排序
     * @return 文章列表
     */
    @GetMapping("/article/listarticles")
    @LogView(module = 0)
    public Response<PageData<ArticleVO>> selectPortalPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                                 @RequestParam("latest") Boolean latest, @RequestParam("categoryId") Long categoryId,
                                 @RequestParam("like") Boolean like, @RequestParam("read") Boolean read) {
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");

        PortalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO = new PortalArticleVOIPageQueryDTO();
        portalArticleVOIPageQueryDTO.setLogStr("con=selectPortalPage");
        portalArticleVOIPageQueryDTO.setPage(page);
        portalArticleVOIPageQueryDTO.setLimit(limit);
        portalArticleVOIPageQueryDTO.setCategoryId(categoryId);
        portalArticleVOIPageQueryDTO.setLatest(latest);
        portalArticleVOIPageQueryDTO.setLike(like);
        portalArticleVOIPageQueryDTO.setRead(read);
        portalArticleVOIPageQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                .tagList(true)
                .author(true)
                .build());

        return Response.success(articleService.selectPortalPage(portalArticleVOIPageQueryDTO));
    }

    /**
     * 分页查询首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @GetMapping("/article/listhomearticles")
    @LogView(module = 0)
    public Response<PageData<ArticleVO>> selectPortalHomePage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");
        return Response.success(articleService.selectPortalHomePage(page, limit));
    }

    /**
     * 查询ArticleVO对象
     *
     * @param articleId articleId
     * @param password password
     * @return ArticleVO
     */
    @GetMapping("/article/{id}")
    @LogView(module = 0)
    public Response<ArticleVO> selectArticleVOByPassword(@PathVariable Long articleId, @RequestParam(value = "password", required = false, defaultValue = "") String password){
        return Response.success(articleService.selectArticleVOByPassword(articleId, password));
    }

    /**
     * 文章点赞
     *
     * @param articleId articleId
     * @return 点赞结果
     */
    @PutMapping("/article/{articleId}")
    @LogView(module = 0)
    public Response<Void> addArticleLikeNum(@PathVariable Long articleId) throws Exception {
        MyAssert.notNull(articleId, "articleId不能为空");
        articleService.addArticleLikeNum(articleId);
        return Response.success();
    }

    /**
     * 查询热读文章列表
     *
     * @return 热读文章列表
     */
    @GetMapping("/article/listhotreadarticles")
    @LogView(module = 0)
    public Response<List<ArticleVO>> selectHotReadArticleVOs(){
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setLogStr("con=selectHotReadArticleVOs");
        return Response.success(articleService.selectHotReadArticleVOs(baseRequestDTO, ArticleBuilder.builder().build()));
    }

    /**
     * 根据文章id查询公开状态
     *
     * @param articleId 文章id
     * @return 公开状态
     */
    @GetMapping("/article/open")
    @LogView(module = 0)
    public Response<Boolean> selectArticleOpenById(@RequestParam("articleId") Long articleId){
        MyAssert.notNull(articleId, "articleId不能为空");
        return Response.success(articleService.selectArticleOpenById(articleId));
    }

}
