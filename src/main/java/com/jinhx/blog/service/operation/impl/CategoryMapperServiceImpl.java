package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.CategoryRankEnum;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.mapper.operation.CategoryMapper;
import com.jinhx.blog.service.operation.CategoryMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CategoryServiceImpl
 *
 * @author jinhx
 * @since 2018-12-17
 */
@Service
@Slf4j
public class CategoryMapperServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryMapperService {

    /**
     * 树状列表
     *
     * @param module module
     * @return 分类列表
     */
    @Override
    public List<Category> select(Integer module) {
        List<Category> categorys = baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(!Objects.isNull(module), Category::getModule, module));

        //添加顶级分类
        Category root = new Category();
        root.setId(-1);
        root.setName("根目录");
        root.setParentId(-1);
        categorys.add(root);
        return categorys;
    }

    /**
     * 信息
     *
     * @param id id
     * @return Category
     */
    @Override
    public Category info(Integer id) {
        return baseMapper.selectById(id);
    }

    /**
     * 保存
     *
     * @param category category
     */
    @Override
    public void add(Category category) {
        verifyCategory(category);
        baseMapper.insert(category);
    }

    /**
     * 修改
     *
     * @param category category
     */
    @Override
    public void update(Category category) {
        baseMapper.updateById(category);
    }

    /**
     * 删除
     *
     * @param id id
     */
    @Override
    public void delete(Integer id) {
        baseMapper.deleteById(id);
    }

    /**
     * 数据校验
     *
     * @param category category
     */
    private void verifyCategory(Category category) {
        //上级分类级别
        int parentRank = CategoryRankEnum.ROOT.getCode();
        if (category.getParentId() != CategoryRankEnum.FIRST.getCode()
                && category.getParentId() != CategoryRankEnum.ROOT.getCode()) {
            Category parentCategory = info(category.getParentId());
            parentRank = parentCategory.getRank();
        }

        // 一级
        if (category.getRank() == CategoryRankEnum.FIRST.getCode()) {
            if (category.getParentId() != CategoryRankEnum.ROOT.getCode()){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级目录只能为根目录");
            }
        }

        //二级
        if (category.getRank() == CategoryRankEnum.SECOND.getCode()) {
            if (parentRank != CategoryRankEnum.FIRST.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级目录只能为一级类型");
            }
        }

        //三级
        if (category.getRank() == CategoryRankEnum.THIRD.getCode()) {
            if (parentRank != CategoryRankEnum.SECOND.getCode()) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上级目录只能为二级类型");
            }
        }
    }

    /**
     * 根据父级别查询子级别
     *
     * @param id id
     * @return List<Category>
     */
    @Override
    public List<Category> queryListByParentId(Integer id) {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id));
    }

    /**
     * 根据id查询
     *
     * @param id id
     * @return Category
     */
    @Override
    public Category getById(Integer id) {
        return baseMapper.selectById(id);
    }

    /**
     * 根据类别Id数组查询类别数组
     *
     * @param categoryIds categoryIds
     * @param categoryList categoryList
     * @return String
     */
    @Override
    public String renderCategoryArr(String categoryIds, List<Category> categoryList) {
        if (ObjectUtils.isEmpty(categoryIds)) {
            return "";
        }

        List<String> categoryStrList = new ArrayList<>();
        String[] categoryIdArr = categoryIds.split(",");
        for (int i = 0; i < categoryIdArr.length; i++) {
            Integer categoryId = Integer.parseInt(categoryIdArr[i]);
            // 根据Id查找类别名称
            String categoryStr = categoryList
                    .stream()
                    .filter(category -> category.getId().equals(categoryId))
                    .map(Category::getName)
                    .findAny()
                    .orElse("");
            categoryStrList.add(categoryStr);
        }

        return String.join(",",categoryStrList);
    }

    /********************** portal ********************************/

    /**
     * 获取分类列表
     *
     * @param module 模块
     * @return 分类列表
     */
    @Cacheable(value = RedisKeyConstants.CATEGORYS, key = "#module")
    @Override
    public List<Category> listCategories(String module) {
        return baseMapper.selectList(new QueryWrapper<Category>().lambda()
                .eq(ObjectUtils.isNotEmpty(module),Category::getModule,module));
    }

}
