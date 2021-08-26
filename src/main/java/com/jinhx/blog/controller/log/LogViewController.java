package com.jinhx.blog.controller.log;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.log.vo.HomeLogInfoVO;
import com.jinhx.blog.service.log.LogViewService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * LogViewController
 *
 * @author jinhx
 * @since 2019-02-24
 */
@RestController
public class LogViewController {

    @Autowired
    private LogViewService logViewService;

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/log/homeinfo")
    @RequiresPermissions("log:list")
    public Response<HomeLogInfoVO> selectHommeLogInfoVO() {
        return Response.success(logViewService.selectHommeLogInfoVO());
    }

    /**
     * 分页查询日志列表
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return 日志列表
     */
    @GetMapping("/manage/log/list")
    @RequiresPermissions("log:list")
    public Response<PageData<LogView>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("module") Integer module) {
        return Response.success(logViewService.selectPage(page, limit, module));
    }

}
