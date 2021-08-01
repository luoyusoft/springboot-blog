package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.service.operation.CategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * CategoryController
 *
 * @author jinhx
 * @since 2018-12-17
 */
@RestController
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 查询所有菜单
     *
     * @param name name
     * @param module module
     * @return List<Category>
     */
    @GetMapping("/manage/operation/category/list")
    @RequiresPermissions("operation:category:list")
    public Response list(@RequestParam("name") String name, @RequestParam("module") Integer module){
        return Response.success(categoryService.queryWithParentName(name, module));
    }

    /**
     * 树状列表
     *
     * @param module module
     * @return 分类列表
     */
    @GetMapping("/manage/operation/category/select")
    @RequiresPermissions("operation:category:list")
    public Response select(@RequestParam("module") Integer module){
        return Response.success(categoryService.select(module));
    }

    /**
     * 信息
     *
     * @param id id
     * @return Category
     */
    @GetMapping("/manage/operation/category/info/{id}")
    @RequiresPermissions("operation:category:info")
    public Response info(@PathVariable("id") Integer id){
        return Response.success(categoryService.info(id));
    }

    /**
     * 保存
     *
     * @param category category
     */
    @PostMapping("/manage/operation/category/save")
    @RequiresPermissions("operation:category:save")
    public Response save(@RequestBody Category category){
        // 数据校验
        ValidatorUtils.validateEntity(category, AddGroup.class);
        categoryService.add(category);

        return Response.success();
    }

    /**
     * 修改
     *
     * @param category category
     */
    @PutMapping("/manage/operation/category/update")
    @RequiresPermissions("operation:category:update")
    public Response update(@RequestBody Category category){
        categoryService.update(category);
        return Response.success();
    }

    /**
     * 删除
     *
     * @param id id
     */
    @DeleteMapping("/manage/operation/category/delete/{id}")
    @RequiresPermissions("operation:category:delete")
    public Response delete(@PathVariable("id") Integer id){
        categoryService.delete(id);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 获取分类列表
     *
     * @param module 模块
     * @return 分类列表
     */
    @GetMapping("/operation/listcategories")
    public Response listCategories(@RequestParam("module") String module) {
        if (module == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "module不能为空");
        }
        List<Category> categoryList = categoryService.listCategories(module);
        return Response.success(categoryList);
    }

}
