package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.mapper.operation.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * CategoryMapperService
 *
 * @author jinhx
 * @since 2018-12-17
 */
@Service
public class CategoryMapperService extends ServiceImpl<CategoryMapper, Category> {

    /**
     * 根据父主键名称，模块查询类别列表
     *
     * @param name name
     * @param module module
     * @return 类别列表
     */
    public List<Category> selectCategoryVOsByParentNameAndModule(String name, Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(StringUtils.isNotBlank(name), Category::getName, name)
                .like(Objects.nonNull(module), Category::getModule, module));
    }

    /**
     * 根据模块查询类别列表
     *
     * @param module module
     * @return 类别列表
     */
    public List<Category> selectCategorysByModule(Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Objects.nonNull(module), Category::getModule, module));
    }

    /**
     * 根据categoryId查询类别
     *
     * @param categoryId categoryId
     * @return 类别
     */
    public Category selectCategoryById(Long categoryId) {
        List<Category> categorys = selectCategorysById(Lists.newArrayList(categoryId));
        if (CollectionUtils.isEmpty(categorys)){
            return null;
        }

        return categorys.get(0);
    }

    /**
     * 根据categoryId查询类别列表
     *
     * @param categoryIds categoryIds
     * @return 类别列表
     */
    public List<Category> selectCategorysById(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Category>().in(Category::getCategoryId, categoryIds));
    }

    /**
     * 新增类别
     *
     * @param category category
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertCategory(Category category) {
        insertCategorys(Lists.newArrayList(category));
    }

    /**
     * 批量新增类别
     *
     * @param categorys categorys
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertCategorys(List<Category> categorys) {
        if (CollectionUtils.isNotEmpty(categorys)){
            if (categorys.stream().mapToInt(item -> baseMapper.insert(item)).sum() != categorys.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据categoryId更新类别
     *
     * @param category category
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryById(Category category) {
        updateCategorysById(Lists.newArrayList(category));
    }

    /**
     * 批量根据categoryId更新类别
     *
     * @param categorys categorys
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCategorysById(List<Category> categorys) {
        if (CollectionUtils.isNotEmpty(categorys)){
            if (categorys.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != categorys.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 根据parentId查询子类别列表
     *
     * @param parentId parentId
     * @return 类别列表
     */
    public List<Category> selectCategorysByParentId(Long parentId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId));
    }

    /**
     * 根据categoryId删除类别
     *
     * @param categoryId categoryId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryById(Long categoryId) {
        deleteCategorysById(Lists.newArrayList(categoryId));
    }

    /**
     * 批量根据categoryId删除类别
     *
     * @param categoryIds categoryIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategorysById(List<Long> categoryIds) {
        if (CollectionUtils.isNotEmpty(categoryIds)){
            if (baseMapper.deleteBatchIds(categoryIds) != categoryIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

}
