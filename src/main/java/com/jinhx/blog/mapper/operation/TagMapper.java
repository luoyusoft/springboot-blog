package com.jinhx.blog.mapper.operation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.vo.TagVO;

import java.util.List;

/**
 * TagMapper
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface TagMapper extends BaseMapper<Tag> {

    /********************** portal ********************************/

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    List<TagVO> selectTagVOsByArticle(Integer module);

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    List<TagVO> selectTagVOsByVideo(Integer module);

}
