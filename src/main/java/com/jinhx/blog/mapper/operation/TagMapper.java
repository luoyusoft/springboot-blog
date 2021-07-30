package com.jinhx.blog.mapper.operation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.vo.TagVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TagMapper
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据linkId获取Tag列表
     * @param linkId
     * @param module
     * @return
     */
    List<Tag> listByLinkId(@Param("linkId") Integer linkId, @Param("module") Integer module);

    /**
     * 根据linkId删除多对多关联
     * @param linkId
     * @param module
     */
    void deleteTagLink(@Param("linkId") Integer linkId, @Param("module") Integer module);

    /********************** portal ********************************/

    /**
     * 获取tagVoList
     * @return
     */
    List<TagVO> listTagArticleDTO(Integer module);

    /**
     * 获取tagVoList
     * @return
     */
    List<TagVO> listTagVideoDTO(Integer module);

}
