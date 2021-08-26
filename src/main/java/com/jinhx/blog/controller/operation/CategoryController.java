package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.vo.CategoryVO;
import com.jinhx.blog.service.operation.CategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController
 *
 * @author jinhx
 * @since 2018-12-17
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父主键名称，模块查询类别列表
     *
     * @param name name
     * @param module module
     * @return 类别列表
     */
    @GetMapping("/manage/operation/category/list")
    @RequiresPermissions("operation:category:list")
    public Response<List<CategoryVO>> selectCategoryVOsByParentNameAndModule(@RequestParam("name") String name, @RequestParam("module") Integer module){
        return Response.success(categoryService.selectCategoryVOsByParentNameAndModule(name, module));
    }

    /**
     * 根据模块查询类别列表
     *
     * @param module module
     * @return 类别列表
     */
    @GetMapping("/manage/operation/category/select")
    @RequiresPermissions("operation:category:list")
    public Response<List<Category>> selectCategorysByModule(@RequestParam("module") Integer module){
        return Response.success(categoryService.selectCategorysByModule(module));
    }

    /**
     * 根据categoryId查询类别
     *
     * @param categoryId categoryId
     * @return 类别
     */
    @GetMapping("/manage/operation/category/info/{id}")
    @RequiresPermissions("operation:category:info")
    public Response<Category> selectCategoryById(@PathVariable("id") Long categoryId){
        return Response.success(categoryService.selectCategoryById(categoryId));
    }

    /**
     * 新增类别
     *
     * @param category category
     * @return 新增结果
     */
    @PostMapping("/manage/operation/category/save")
    @RequiresPermissions("operation:category:save")
    public Response<Void> insertCategory(@RequestBody Category category){
        ValidatorUtils.validateEntity(category, InsertGroup.class);
        categoryService.insertCategory(category);
        return Response.success();
    }

    /**
     * 根据categoryId更新类别
     *
     * @param category category
     * @return 更新结果
     */
    @PutMapping("/manage/operation/category/update")
    @RequiresPermissions("operation:category:update")
    public Response<Void> updateCategoryById(@RequestBody Category category){
        categoryService.updateCategoryById(category);
        return Response.success();
    }

    /**
     * 根据categoryId删除类别
     *
     * @param categoryId categoryId
     * @return 删除结果
     */
    @DeleteMapping("/manage/operation/category/delete/{id}")
    @RequiresPermissions("operation:category:delete")
    public Response<Void> deleteCategoryById(@PathVariable("id") Long categoryId){
        categoryService.deleteCategoryById(categoryId);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询类别列表
     *
     * @param module 模块
     * @return 类别列表
     */
    @GetMapping("/operation/listcategories")
    public Response<List<Category>> selectPortalCategorysByModule(@RequestParam("module") Integer module) {
        MyAssert.notNull(module, "module不能为空");
        return Response.success(categoryService.selectPortalCategorysByModule(module));
    }

}
