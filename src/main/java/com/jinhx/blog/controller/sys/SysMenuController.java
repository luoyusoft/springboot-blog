package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.enums.MenuTypeEnum;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysMenu;
import com.jinhx.blog.entity.sys.vo.SysMenuVO;
import com.jinhx.blog.service.sys.ShiroService;
import com.jinhx.blog.service.sys.SysMenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * SysMenuController
 *
 * @author jinhx
 * @since 2018-10-19
 */
@RestController
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private ShiroService shiroService;

    /**
     * 获取用户的所有菜单列表
     *
     * @return 用户的所有菜单列表
     */
    @GetMapping("/manage/sys/menu/nav")
    public Response<SysMenuVO> nav(){
        List<SysMenu> menuList = sysMenuService.selectSysMenusBySysUserId(SysAdminUtils.getSysUserId());
        Set<String> permissions = shiroService.getUserPermissions(SysAdminUtils.getSysUserId());
        SysMenuVO sysMenuVO = new SysMenuVO();
        sysMenuVO.setMenuList(menuList);
        sysMenuVO.setPermissions(permissions);
        return Response.success(sysMenuVO);
    }

    /**
     * 查询所有菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("/manage/sys/menu/list")
    @RequiresPermissions("sys:menu:list")
    public Response<List<SysMenu>> selectAllSysMenus(){
        return Response.success(sysMenuService.selectAllSysMenus());
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("/manage/sys/menu/select")
    @RequiresPermissions("sys:menu:select")
    public Response<List<SysMenu>> selectNotButtonSysMenus(){
        //查询列表数据
        List<SysMenu> menuList = sysMenuService.selectNotButtonSysMenus();

        //添加顶级菜单
        SysMenu root = new SysMenu();
        root.setSysMenuId(SysMenu.ONE_SYS_MENU_ID);
        root.setName(SysMenu.ONE_NAME);
        root.setParentId(SysMenu.ONE_PARENT_ID);
        root.setOpen(true);
        menuList.add(root);

        return Response.success(menuList);
    }

    /**
     * 根据sysMenuId查询菜单
     *
     * @param sysMenuId sysMenuId
     * @return 菜单
     */
    @GetMapping("/manage/sys/menu/info/{sysMenuId}")
    @RequiresPermissions("sys:menu:info")
    public Response<SysMenu> selectSysMenuById(@PathVariable Long sysMenuId){
        return Response.success(sysMenuService.selectSysMenuById(sysMenuId));
    }

    /**
     * 新增菜单
     *
     * @param sysMenu sysMenu
     * @return 新增结果
     */
    @PostMapping("/manage/sys/menu/save")
    @RequiresPermissions("sys:menu:save")
    public Response<Void> insertSysMenu(@RequestBody SysMenu sysMenu){
        ValidatorUtils.validateEntity(sysMenu, InsertGroup.class);
        //数据校验
        verifyForm(sysMenu);
        sysMenuService.insertSysMenu(sysMenu);
        return Response.success();
    }

    /**
     * 根据sysMenuId更新菜单
     *
     * @param sysMenu sysMenu
     * @return 更新结果
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/menu/update")
    @RequiresPermissions("sys:menu:update")
    public Response<Void> updateSysMenuById(@RequestBody SysMenu sysMenu){
        ValidatorUtils.validateEntity(sysMenu, UpdateGroup.class);
        // 数据校验
        verifyForm(sysMenu);
        sysMenuService.updateSysMenuById(sysMenu);
        return Response.success();
    }

    /**
     * 根据sysMenuId删除菜单
     *
     * @param sysMenuId sysMenuId
     * @return 删除结果
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/menu/delete/{sysMenuId}")
    @RequiresPermissions("sys:menu:delete")
    public Response<Void> deleteSysMenuById(@PathVariable Long sysMenuId){
        sysMenuService.deleteSysMenuById(sysMenuId);
        return Response.success();
    }

    /**
     * 验证参数是否正确
     *
     * @param sysMenu sysMenu
     */
    private void verifyForm(SysMenu sysMenu) {
        //菜单
        if (sysMenu.getType() == MenuTypeEnum.MENU.getCode()) {
            if (StringUtils.isBlank(sysMenu.getUrl())) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = MenuTypeEnum.CATALOG.getCode();
        if (!Objects.equals(sysMenu.getParentId(), SysMenu.ONE_SYS_MENU_ID)) {
            SysMenu parentSysMenu = sysMenuService.selectSysMenuById(sysMenu.getParentId());
            parentType = parentSysMenu.getType();
        }

        //目录、菜单
        if (sysMenu.getType() == MenuTypeEnum.CATALOG.getCode() ||
                sysMenu.getType() == MenuTypeEnum.MENU.getCode()) {
            if (parentType != MenuTypeEnum.CATALOG.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级菜单只能为目录类型");
            }
        }

        //按钮
        if (sysMenu.getType() == MenuTypeEnum.BUTTON.getCode()) {
            if (parentType != MenuTypeEnum.MENU.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级菜单只能为菜单类型");
            }
        }
    }

}
