package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.enums.MenuTypeEnum;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
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
    public Response nav(){
        List<SysMenu> menuList = sysMenuService.listUserMenu(SysAdminUtils.getUserId());
        Set<String> permissions = shiroService.getUserPermissions(SysAdminUtils.getUserId());
        SysMenuVO sysMenuVO = new SysMenuVO();
        sysMenuVO.setMenuList(menuList);
        sysMenuVO.setPermissions(permissions);
        return Response.success(sysMenuVO);
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/manage/sys/menu/list")
    @RequiresPermissions("sys:menu:list")
    public Response list(){
        List<SysMenu> menuList = sysMenuService.list(null);
        menuList.forEach(sysMenu -> {
            SysMenu parentMenu = sysMenuService.getById(sysMenu.getParentId());
            if(parentMenu != null){
                sysMenu.setParentName(parentMenu.getName());
            }
        });
        return Response.success(menuList);
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("/manage/sys/menu/select")
    @RequiresPermissions("sys:menu:select")
    public Response select(){
        //查询列表数据
        List<SysMenu> menuList = sysMenuService.queryNotButtonList();

        //添加顶级菜单
        SysMenu root = new SysMenu();
        root.setId(0);
        root.setName("一级菜单");
        root.setParentId(-1);
        root.setOpen(true);
        menuList.add(root);

        return Response.success(menuList);
    }

    /**
     * 获取单个菜单信息
     *
     * @param menuId 菜单id
     * @return 菜单信息
     */
    @GetMapping("/manage/sys/menu/info/{menuId}")
    @RequiresPermissions("sys:menu:info")
    public Response update(@PathVariable("menuId") Integer menuId){
        return Response.success(sysMenuService.getById(menuId));
    }

    /**
     * 保存
     *
     * @param menu menu
     */
    @PostMapping("/manage/sys/menu/save")
    @RequiresPermissions("sys:menu:save")
    public Response save(@RequestBody SysMenu menu){
        //数据校验
        verifyForm(menu);
        sysMenuService.save(menu);

        return Response.success();
    }

    /**
     * 更新
     *
     * @param menu menu
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/menu/update")
    @RequiresPermissions("sys:menu:update")
    public Response update(@RequestBody SysMenu menu){
        //数据校验
        verifyForm(menu);
        sysMenuService.updateById(menu);

        return Response.success();
    }

    /**
     * 删除
     *
     * @param menuId menuId
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/menu/delete/{menuId}")
    @RequiresPermissions("sys:menu:delete")
    public Response delete(@PathVariable("menuId") Integer menuId){
        //判断是否有子菜单或按钮
        List<SysMenu> menuList = sysMenuService.queryListParentId(menuId);
        if(menuList.size() > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请先删除子菜单或按钮");
        }
        sysMenuService.delete(menuId);
        return Response.success();
    }

    /**
     * 验证参数是否正确
     *
     * @param menu menu
     */
    private void verifyForm(SysMenu menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级菜单不能为空");
        }

        //菜单
        if (menu.getType() == MenuTypeEnum.MENU.getCode()) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = MenuTypeEnum.CATALOG.getCode();
        if (menu.getParentId() != 0) {
            SysMenu parentMenu = sysMenuService.getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == MenuTypeEnum.CATALOG.getCode() ||
                menu.getType() == MenuTypeEnum.MENU.getCode()) {
            if (parentType != MenuTypeEnum.CATALOG.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级菜单只能为目录类型");
            }
        }

        //按钮
        if (menu.getType() == MenuTypeEnum.BUTTON.getCode()) {
            if (parentType != MenuTypeEnum.MENU.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级菜单只能为菜单类型");
            }
        }
    }

}
