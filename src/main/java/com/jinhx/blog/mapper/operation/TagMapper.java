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
     * 获取tagVos
     *
     * @param module module
     * @return List<TagVO>
     */
    List<TagVO> listTagVOsByArticle(Integer module);

    /**
     * 获取tagVos
     *
     * @param module module
     * @return List<TagVO>
     */
    List<TagVO> listTagVOsByVideo(Integer module);

}
