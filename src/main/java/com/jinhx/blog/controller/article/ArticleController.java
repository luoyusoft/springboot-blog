package com.jinhx.blog.controller.article;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * ArticleController
 *
 * @author jinhx
 * @since 2018-11-20
 */
@RestController
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/article/homeinfo")
    @RequiresPermissions("article:list")
    public Response<HomeArticleInfoVO> getHomeArticleInfoVO() {
        return Response.success(articleService.getHomeArticleInfoVO());
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
    public Response<PageData<ArticleVO>> listArticle(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("title") String title) {
        if (Objects.isNull(page) || Objects.isNull(limit)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "page，limit不能为空");
        }

        ArticleVOIPageQueryDTO articleVOIPageQueryDTO = new ArticleVOIPageQueryDTO();
        articleVOIPageQueryDTO.setLogStr("con=listArticle");
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

        return Response.success(articleService.queryPage(articleVOIPageQueryDTO));
    }

    /**
     * 信息
     *
     * @param articleId 文章id
     * @return 文章信息
     */
    @GetMapping("/manage/article/info/{articleId}")
    @RequiresPermissions("article:list")
    public Response<ArticleVO> info(@PathVariable("articleId") Integer articleId) {
        if (Objects.isNull(articleId)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "articleId不能为空");
        }

        ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
        articleVOsQueryDTO.setLogStr("con=info");
        articleVOsQueryDTO.setArticleIds(Lists.newArrayList(articleId));
        articleVOsQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                .categoryListStr(true)
                .tagList(true)
                .recommend(true)
                .top(true)
                .author(true)
                .build());

        List<ArticleVO> articleVOs = articleService.getArticleVOs(articleVOsQueryDTO);
        if (CollectionUtils.isEmpty(articleVOs)){
            return Response.success();
        }

        return Response.success(articleVOs.get(0));
    }

    /**
     * 保存文章
     *
     * @param articleVO 文章信息
     */
    @PostMapping("/manage/article/save")
    @RequiresPermissions("article:save")
    public Response<Void> saveArticle(@RequestBody ArticleVO articleVO){
        ValidatorUtils.validateEntity(articleVO, AddGroup.class);
        articleService.saveArticle(articleVO);

        return Response.success();
    }

    /**
     * 更新文章
     *
     * @param articleVO 文章信息
     */
    @PutMapping("/manage/article/update")
    @RequiresPermissions("article:update")
    public Response<Void> updateArticle(@RequestBody ArticleVO articleVO){
        articleService.updateArticle(articleVO);
        return Response.success();
    }

    /**
     * 更新文章状态
     *
     * @param articleVO 文章信息
     */
    @PutMapping("/manage/article/update/status")
    @RequiresPermissions("article:update")
    public Response<Void> updateArticleStatus(@RequestBody ArticleVO articleVO){
        articleService.updateArticleStatus(articleVO);
        return Response.success();
    }

    /**
     * 批量删除
     *
     * @param articleIds 文章id列表
     */
    @DeleteMapping("/manage/article/delete")
    @RequiresPermissions("article:delete")
    public Response deleteArticles(@RequestBody List<Integer> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "articleIds不能为空");
        }

        if (articleIds.size() > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "articleIds不能超过100个");
        }

        articleService.deleteArticles(articleIds);
        return Response.success();
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
    @GetMapping("/article/listarticles")
    @LogView(module = 0)
    public Response<PageData<ArticleVO>> listArticleVOs(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                                 @RequestParam("latest") Boolean latest, @RequestParam("categoryId") Integer categoryId,
                                 @RequestParam("like") Boolean like, @RequestParam("read") Boolean read) {
        if (Objects.isNull(page) || Objects.isNull(limit)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "page，limit不能为空");
        }

        PortalArticleVOIPageQueryDTO portalArticleVOIPageQueryDTO = new PortalArticleVOIPageQueryDTO();
        portalArticleVOIPageQueryDTO.setLogStr("con=listArticleVOs");
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

        return Response.success(articleService.listArticleVOs(portalArticleVOIPageQueryDTO));
    }

    /**
     * 分页获取首页文章列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 首页文章列表
     */
    @GetMapping("/article/listhomearticles")
    @LogView(module = 0)
    public Response listHomeArticles(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        return Response.success(articleService.listHomeArticles(page, limit));
    }

    /**
     * 获取ArticleVO对象
     *
     * @param id id
     * @param password password
     * @return ArticleVO
     */
    @GetMapping("/article/{id}")
    @LogView(module = 0)
    public Response<ArticleVO> getArticle(@PathVariable Integer id, @RequestParam(value = "password", required = false, defaultValue = "") String password){
        return Response.success(articleService.getArticleVOByPassword(id, password));
    }

    /**
     * 文章点赞
     *
     * @param id articleId
     * @return Response
     */
    @PutMapping("/article/{id}")
    @LogView(module = 0)
    public Response<Void> updateArticle(@PathVariable Integer id) throws Exception {
        if (Objects.isNull(id)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "id不能为空");
        }

        articleService.updateArticle(id);

        return Response.success();
    }

    /**
     * 获取热读榜
     *
     * @return 热读文章列表
     */
    @GetMapping("/article/listhotreadarticles")
    @LogView(module = 0)
    public Response<List<ArticleVO>> listHotReadArticles(){
        BaseRequestDTO baseRequestDTO = new BaseRequestDTO();
        baseRequestDTO.setLogStr("con=listHotReadArticles");

        return Response.success(articleService.listHotReadArticles(baseRequestDTO, ArticleBuilder.builder().build()));
    }

    /**
     * 根据文章id获取公开状态
     *
     * @param id 文章id
     * @return 公开状态
     */
    @GetMapping("/article/open")
    @LogView(module = 0)
    public Response getArticleOpenById(@RequestParam("id") Integer id){
        return Response.success(articleService.getArticleOpenById(id));
    }

}
