package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.builder.CategoryAdaptorBuilder;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.vo.CategoryVO;

import java.util.List;

/**
 * CategoryService
 *
 * @author jinhx
 * @since 2018-12-17
 */
public interface CategoryService extends IService<Category> {

    /**
     * 将Category转换为CategoryVO
     *
     * @param categoryAdaptorBuilder categoryAdaptorBuilder
     * @return CategoryVO
     */
    CategoryVO adaptorCategoryToCategoryVO(CategoryAdaptorBuilder<Category> categoryAdaptorBuilder);

    /**
     * 将Category列表按需转换为CategoryVO列表
     *
     * @param categoryAdaptorBuilder categoryAdaptorBuilder
     * @return CategoryVO列表
     */
    List<CategoryVO> adaptorCategorysToCategoryVOs(CategoryAdaptorBuilder<List<Category>> categoryAdaptorBuilder);

    /**
     * 树状列表
     *
     * @param module module
     * @return 分类列表
     */
    List<Category> select(Integer module);

    /**
     * 信息
     *
     * @param id id
     * @return Category
     */
    Category info(Integer id);

    /**
     * 保存
     *
     * @param category category
     */
    void add(Category category);

    /**
     * 修改
     *
     * @param category category
     */
    void update(Category category);

    /**
     * 删除
     *
     * @param id id
     */
    void delete(Integer id);

    /**
     * 查询所有菜单
     *
     * @param name name
     * @param module module
     * @return List<Category>
     */
    List<CategoryVO> queryWithParentName(String name, Integer module);

    /**
     * 根据父级别查询子级别
     *
     * @param id id
     * @return List<Category>
     */
    List<Category> queryListByParentId(Integer id);

    /**
     * 根据id查询
     *
     * @param id id
     * @return Category
     */
    Category getById(Integer id);

    /**
     * 根据类别Id数组查询类别数组
     *
     * @param categoryIds categoryIds
     * @param categoryList categoryList
     * @return String
     */
    String renderCategoryArr(String categoryIds, List<Category> categoryList);

    /********************** portal ********************************/

    /**
     * 获取分类列表
     *
     * @param module 模块
     * @return 分类列表
     */
    List<Category> listCategories(String module);

}
