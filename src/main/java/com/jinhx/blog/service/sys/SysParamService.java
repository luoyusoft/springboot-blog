package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysParam;

import java.util.List;

/**
 * SysParamService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysParamService {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageUtils
     */
     PageData queryPage(Integer page, Integer limit, String menuUrl, String type);

    /**
     * 获取所有参数列表
     *
     * @return 所有参数列表
     */
    List<SysParam> list();

    /**
     * 信息
     *
     * @param id id
     * @return 信息
     */
    SysParam getById(Integer id);

    /**
     * 保存
     *
     * @param sysParam sysParam
     */
    void save(SysParam sysParam);

    /**
     * 修改
     *
     * @param sysParam sysParam
     */
    void updateById(SysParam sysParam);

    /**
     * 根据角色id列表批量删除角色
     *
     * @param roleIds 角色id列表
     */
    void deleteBatch(List<Integer> roleIds);

}
