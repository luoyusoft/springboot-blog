package com.jinhx.blog.adaptor.operation;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.vo.CategoryVO;
import com.jinhx.blog.service.operation.CategoryMapperService;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * CategoryAdaptor
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Component
public class CategoryAdaptor {

    @Autowired
    private CategoryMapperService categoryMapperService;

    /**
     * 将Category转换为CategoryVO
     *
     * @param categoryAdaptorBuilder categoryAdaptorBuilder
     * @return CategoryVO
     */
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

}
