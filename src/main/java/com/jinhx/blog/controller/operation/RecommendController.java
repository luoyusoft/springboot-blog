package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.entity.operation.vo.RecommendVO;
import com.jinhx.blog.service.operation.RecommendService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * RecommendController
 *
 * @author jinhx
 * @since 2019-02-22
 */
@RestController
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/operation/recommend/homeinfo")
    public Response getHomeRecommendInfoVO() {
        HomeRecommendInfoVO homeRecommendInfoVO = recommendService.getHomeRecommendInfoVO();
        return Response.success(homeRecommendInfoVO);
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    @GetMapping("/manage/operation/recommend/list")
    @RequiresPermissions("operation:recommend:list")
    public Response list(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit){
        PageUtils recommendPage = recommendService.queryPage(page, limit);
        return Response.success(recommendPage);
    }

    /**
     * 获取推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    @GetMapping("/manage/operation/recommend/select")
    @RequiresPermissions("operation:recommend:list")
    public Response select(@RequestParam("module") Integer module, @RequestParam("title") String title) {
        if(module == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "module不能为空");
        }
        List<RecommendVO> recommendList = recommendService.select(module, title);
        return Response.success(recommendList);
    }

    /**
     * 信息
     *
     * @param id id
     * @return 信息
     */
    @GetMapping("/manage/operation/recommend/info/{id}")
    @RequiresPermissions("operation:recommend:info")
    public Response info(@PathVariable("id") String id){
       Recommend recommend = recommendService.getById(id);
        return Response.success(recommend);
    }

    /**
     * 新增
     *
     * @param recommend recommend
     */
    @PostMapping("/manage/operation/recommend/save")
    @RequiresPermissions("operation:recommend:save")
    public Response save(@RequestBody Recommend recommend){
        if(recommend.getLinkId() == null || recommend.getModule() == null || recommend.getOrderNum() == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "linkId，module，orderNum不能为空");
        }
        ValidatorUtils.validateEntity(recommend, AddGroup.class);
        recommendService.insertRecommend(recommend);

        return Response.success();
    }

    /**
     * 更新
     *
     * @param recommend recommend
     */
    @PutMapping("/manage/operation/recommend/update")
    @RequiresPermissions("operation:recommend:update")
    public Response update(@RequestBody Recommend recommend){
        if(recommend.getId() == null || recommend.getLinkId() == null
                || recommend.getModule() == null || recommend.getOrderNum() == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "id，linkId，module，orderNum不能为空");
        }
        recommendService.updateRecommend(recommend);

        return Response.success();
    }

    /**
     * 推荐置顶
     *
     * @param id id
     */
    @PutMapping("/manage/operation/recommend/top/{id}")
    @RequiresPermissions("operation:recommend:update")
    public Response updateTop(@PathVariable("id") Integer id){
        if(id == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "id不能为空");
        }
        recommendService.updateRecommendTop(id);

        return Response.success();
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @DeleteMapping("/manage/operation/recommend/delete")
    @RequiresPermissions("operation:recommend:delete")
    public Response deleteRecommendsByIds(@RequestBody Integer[] ids){
        if (ids == null || ids.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能为空");
        }

        if (ids.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能超过100个");
        }

        recommendService.deleteRecommendsByIds(Arrays.asList(ids));
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 获取推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    @RequestMapping("/operation/listrecommends")
    public Response listRecommends(@RequestParam("module") Integer module) {
        if (module == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "module不能为空");
        }

        List<RecommendVO> recommendList = recommendService.listRecommends(module);
        return Response.success(recommendList);
    }

}
