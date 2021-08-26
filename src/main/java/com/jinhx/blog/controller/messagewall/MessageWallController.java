package com.jinhx.blog.controller.messagewall;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.FormatUtils;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.messagewall.vo.HomeMessageWallInfoVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallListVO;
import com.jinhx.blog.service.messagewall.MessageWallService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * MessageWallController
 *
 * @author jinhx
 * @since 2018-11-24
 */
@RestController
public class MessageWallController {

    @Autowired
    private MessageWallService messageWallService;

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/messagewall/homeinfo")
    @RequiresPermissions("messagewall:list")
    public Response<HomeMessageWallInfoVO> selectHomeMessageWallInfoVO() {
        return Response.success(messageWallService.selectHomeMessageWallInfoVO());
    }

    /**
     * 分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @param name 昵称
     * @param floorNum 楼层数
     * @return 留言列表
     */
    @GetMapping("/manage/messagewalls")
    @RequiresPermissions("messagewall:list")
    public Response<PageData<MessageWall>> selectMessageWallPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                                                                 @RequestParam("name") String name, @RequestParam("floorNum") Integer floorNum){
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");
        return Response.success(messageWallService.selectMessageWallPage(page, limit, name, floorNum));
    }

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     * @return 新增结果
     */
    @PostMapping("/manage/messagewall")
    @RequiresPermissions("messagewall:add")
    public Response<Void> insertMessageWall(@RequestBody MessageWall messageWall){
        ValidatorUtils.validateEntity(messageWall, InsertGroup.class);
        messageWallService.insertMessageWall(messageWall);
        return Response.success();
    }

    /**
     * 批量根据messageWallId删除留言
     *
     * @param messageWallIds messageWallIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/messagewall")
    @RequiresPermissions("messagewall:delete")
    public Response<Void> deleteMessageWallsById(@RequestBody List<Long> messageWallIds) {
        MyAssert.sizeBetween(messageWallIds, 1, 100, "messageWallIds");
        messageWallService.deleteMessageWallsById(messageWallIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     * @return 新增结果
     */
    @PostMapping("/messagewall")
    public Response<Void> insertPortalMessageWall(@RequestBody MessageWall messageWall){
        ValidatorUtils.validateEntity(messageWall, InsertGroup.class);
        if (!StringUtils.isEmpty(messageWall.getEmail())){
            if(!FormatUtils.checkEmail(messageWall.getEmail())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "邮箱格式不对");
            }
            if (messageWall.getEmail().length() > 50){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "邮箱长度不能大于50");
            }
        }

        if (!StringUtils.isEmpty(messageWall.getWebsite())){
            if(!messageWall.getWebsite().startsWith("https://") && !messageWall.getWebsite().startsWith("http://")){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "网址格式不对");
            }
            if (messageWall.getWebsite().length() > 1000){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "网址长度不能大于1000");
            }
        }

        // 可以替换大部分空白字符，不限于空格
        messageWall.getName().replaceAll("\\s*", "");

        messageWallService.insertPortalMessageWall(messageWall);

        return Response.success();
    }

    /**
     * 按楼层分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @return 留言列表
     */
    @GetMapping("/messagewall/listmessagewalls")
    @LogView(module = 5)
    public Response<MessageWallListVO> selectPortalMessageWallPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");
        return Response.success(messageWallService.selectPortalMessageWallPage(page, limit));
    }

}
