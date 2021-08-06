package com.jinhx.blog.service.operation.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.entity.builder.CategoryAdaptorBuilder;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.CategoryRankEnum;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.vo.CategoryVO;
import com.jinhx.blog.mapper.operation.CategoryMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.operation.CategoryMapperService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private VideoMapperService videoMapperService;

    @Resource
    private ArticleMapperService articleMapperService;

    @Resource
    private CategoryMapperService categoryMapperService;

    @Autowired
    private CacheServer cacheServer;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 将Category转换为CategoryVO
     *
     * @param categoryAdaptorBuilder categoryAdaptorBuilder
     * @return CategoryVO
     */
    @Override
    public CategoryVO adaptorCategoryToCategoryVO(CategoryAdaptorBuilder<Category> categoryAdaptorBuilder){
        if(ObjectUtils.isNull(categoryAdaptorBuilder) || ObjectUtils.isNull(categoryAdaptorBuilder.getData())){
            return null;
        }
        Category category = categoryAdaptorBuilder.getData();
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);

        if (categoryAdaptorBuilder.getParentName()){
            Category parentCategory = categoryMapperService.getById(categoryVO.getParentId());
            if (ObjectUtils.isNotNull(parentCategory)){
                categoryVO.setParentName(parentCategory.getName());
            }
        }

        return categoryVO;
    }

    /**
     * 将Category列表按需转换为CategoryVO列表
     *
     * @param categoryAdaptorBuilder categoryAdaptorBuilder
     * @return CategoryVO列表
     */
    @Override
    public List<CategoryVO> adaptorCategorysToCategoryVOs(CategoryAdaptorBuilder<List<Category>> categoryAdaptorBuilder){
        if(ObjectUtils.isNull(categoryAdaptorBuilder) || CollectionUtils.isEmpty(categoryAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<CategoryVO> categoryVOs = Lists.newArrayList();
        categoryAdaptorBuilder.getData().forEach(category -> {
            if (ObjectUtils.isNull(category)){
                return;
            }

            categoryVOs.add(adaptorCategoryToCategoryVO(new CategoryAdaptorBuilder.Builder<Category>()
                    .setParentName(categoryAdaptorBuilder.getParentName())
                    .build(category)));
        });

        return categoryVOs;
    }

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

        cleanCategorysAllCache();
    }

    /**
     * 修改
     *
     * @param category category
     */
    @Override
    public void update(Category category) {
        baseMapper.updateById(category);

        cleanCategorysAllCache();
    }

    /**
     * 删除
     *
     * @param id id
     */
    @Override
    public void delete(Integer id) {
        //判断是否有子菜单或按钮
        List<Category> categorys = queryListByParentId(id);
        if(!CollectionUtils.isEmpty(categorys)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请先删除子级别");
        }
        // 判断是否有文章
        if(articleMapperService.checkByCategoryId(id)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该类别下有文章，无法删除");
        }
        // 判断是否有视频
        if(videoMapperService.checkByCategory(id)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该类别下有视频，无法删除");
        }

        baseMapper.deleteById(id);

        cleanCategorysAllCache();
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
     * 查询所有菜单
     *
     * @param name name
     * @param module module
     * @return List<Category>
     */
    @Override
    public List<CategoryVO> queryWithParentName(String name, Integer module) {
        List<Category> categories = baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(!ObjectUtil.isNotNull(module), Category::getModule, module)
                .like(!ObjectUtil.isNotEmpty(name), Category::getName, name));

        if (CollectionUtils.isEmpty(categories)){
            return Collections.emptyList();
        }

        return adaptorCategorysToCategoryVOs(new CategoryAdaptorBuilder.Builder<List<Category>>()
                .setParentName()
                .build(categories));
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

    /**
     * 清除缓存
     */
    private void cleanCategorysAllCache(){
        taskExecutor.execute(() ->{
            cacheServer.cleanCategorysAllCache();
        });
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
