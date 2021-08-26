package com.jinhx.blog.service.operation;

import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.vo.CategoryVO;

import java.util.List;

/**
 * CategoryService
 *
 * @author jinhx
 * @since 2018-12-17
 */
public interface CategoryService {

    /**
     * 根据父主键名称，模块查询类别列表
     *
     * @param name name
     * @param module module
     * @return 类别列表
     */
    List<CategoryVO> selectCategoryVOsByParentNameAndModule(String name, Integer module);

    /**
     * 根据模块查询类别列表
     *
     * @param module module
     * @return 类别列表
     */
    List<Category> selectCategorysByModule(Integer module);

    /**
     * 根据categoryId查询类别
     *
     * @param categoryId categoryId
     * @return 类别
     */
    Category selectCategoryById(Long categoryId);

    /**
     * 新增类别
     *
     * @param category category
     */
    void insertCategory(Category category);

    /**
     * 根据categoryId更新类别
     *
     * @param category category
     */
    void updateCategoryById(Category category);

    /**
     * 根据categoryId删除类别
     *
     * @param categoryId categoryId
     */
    void deleteCategoryById(Long categoryId);

    /**
     * 根据类别Id数组查询类别数组
     *
     * @param categoryIds categoryIds
     * @param module module
     * @return String
     */
    String adaptorcategoryIdsToCategoryNames(String categoryIds, Integer module);

    /********************** portal ********************************/

    /**
     * 根据模块查询类别列表
     *
     * @param module 模块
     * @return 类别列表
     */
    List<Category> selectPortalCategorysByModule(Integer module);

}
