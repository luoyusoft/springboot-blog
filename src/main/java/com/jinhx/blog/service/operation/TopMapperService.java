package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.vo.TopVO;

import java.util.List;

/**
 * TopService
 *
 * @author jinhx
 * @since 2019-02-22
 */
public interface TopMapperService extends IService<Top> {

    /**
     * 置顶
     *
     * @param id id
     */
    void updateTopTop(Integer id);

    /**
     * 删除
     *
     * @param ids ids
     */
    void deleteTopsByIds(List<Integer> ids);

    /**
     * 查找最大顺序
     *
     * @return 最大顺序
     */
    Integer selectTopMaxOrderNum();

    /**
     * 是否已置顶
     *
     * @param module module
     * @param linkId linkId
     * @return 是否已置顶
     */
    Boolean isTopByModuleAndLinkId(Integer module, Integer linkId);

    /********************** portal ********************************/

    /**
     * 查询列表
     *
     * @param module module
     * @return List<Top>
     */
    List<Top> listTops(Integer module);

}
