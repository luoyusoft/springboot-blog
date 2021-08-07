package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.operation.TopAdaptorBuilder;
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
public interface TopService extends IService<Top> {

    /**
     * 将Top转换为TopVO
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO
     */
    TopVO adaptorTopToTopVO(TopAdaptorBuilder<Top> topAdaptorBuilder);

    /**
     * 将Top列表按需转换为TopVO列表
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO列表
     */
    List<TopVO> adaptorTopsToTopVOs(TopAdaptorBuilder<List<Top>> topAdaptorBuilder);

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @return PageUtils
     */
     PageUtils queryPage(Integer page, Integer limit);

    /**
     * 获取置顶列表
     *
     * @param module module
     * @param title title
     * @return List<TopVO>
     */
    List<TopVO> select(Integer module, String title);

    /**
     * 新增
     *
     * @param top top
     */
    void insertTop(Top top);

    /**
     * 更新
     *
     * @param top top
     */
    void updateTop(Top top);

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
     * @return List<TopVO>
     */
    List<TopVO> listTopVO(Integer module);

}
