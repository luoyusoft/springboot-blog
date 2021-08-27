package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;
import com.jinhx.blog.service.operation.FriendLinkService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FriendLinkController
 *
 * @author jinhx
 * @since 2019-02-14
 */
@RestController
@CacheConfig(cacheNames = RedisKeyConstants.FRIENDLINKS)
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/operation/friendlink/homeinfo")
    public Response<HomeFriendLinkInfoVO> selectHommeFriendLinkInfoVO() {
        return Response.success(friendLinkService.selectHommeFriendLinkInfoVO());
    }

    /**
     * 分页查询友链列表
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return 友链列表
     */
    @GetMapping("/manage/operation/friendlink/list")
    @RequiresPermissions("operation:friendlink:list")
    public Response<PageData<FriendLink>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("title") String title){
        return Response.success(friendLinkService.selectPage(page, limit, title));
    }

    /**
     * 根据friendLinkId查询友链
     *
     * @param friendLinkId friendLinkId
     * @return 友链
     */
    @GetMapping("/manage/operation/friendlink/info/{friendLinkId}")
    @RequiresPermissions("operation:friendlink:info")
    public Response<FriendLink> selectFriendLinkById(@PathVariable Long friendLinkId){
        return Response.success(friendLinkService.selectFriendLinkById(friendLinkId));
    }

    /**
     * 新增友链
     *
     * @param friendLink friendLink
     * @return 新增结果
     */
    @PostMapping("/manage/operation/friendlink/save")
    @RequiresPermissions("operation:friendlink:save")
    @CacheEvict(allEntries = true)
    public Response<Void> insertFriendLink(@RequestBody FriendLink friendLink){
        ValidatorUtils.validateEntity(friendLink, InsertGroup.class);
        friendLinkService.insertFriendLink(friendLink);
        return Response.success();
    }

    /**
     * 根据friendLinkId更新友链
     *
     * @param friendLink friendLink
     * @return 更新结果
     */
    @PutMapping("/manage/operation/friendlink/update")
    @RequiresPermissions("operation:friendlink:update")
    @CacheEvict(allEntries = true)
    public Response<Void> updateFriendLinkById(@RequestBody FriendLink friendLink){
        friendLinkService.updateFriendLinkById(friendLink);
        return Response.success();
    }

    /**
     * 批量根据friendLinkId删除友链
     *
     * @param friendLinkIds friendLinkIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/operation/friendlink/delete")
    @RequiresPermissions("operation:friendlink:delete")
    @CacheEvict(allEntries = true)
    public Response<Void> deleteFriendLinksById(@RequestBody List<Long> friendLinkIds){
        MyAssert.sizeBetween(friendLinkIds, 1, 100, "friendLinkIds");
        friendLinkService.deleteFriendLinksById(friendLinkIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 查询友链列表
     *
     * @return 友链列表
     */
    @RequestMapping("/operation/listfriendlinks")
    public Response<List<FriendLink>> selectPortalFriendLinks() {
        return Response.success(friendLinkService.selectPortalFriendLinks());
    }

}
