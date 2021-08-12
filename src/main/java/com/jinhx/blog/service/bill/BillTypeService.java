package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.BillType;

import java.util.List;

/**
 * BillTypeService
 *
 * @author jinhx
 * @since 2021-07-28
 */
public interface BillTypeService extends IService<BillType> {

    /**
     * 查询单个账单类型信息
     *
     * @param billTypeId billTypeId
     * @return 单个账单类型信息
     */
    BillType getBillType(Integer billTypeId);

    /**
     * 查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    PageData queryPage(Integer page, Integer limit, Boolean incomeExpenditureType);


    /**
     * 新增单个账单类型
     *
     * @param billType billType
     * @return 新增结果
     */
    Boolean insertBillType(BillType billType);

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    Boolean insertBillTypes(List<BillType> billTypes);

    /**
     * 更新单个账单类型
     *
     * @param billType billType
     * @return 更新结果
     */
    Boolean updateBillType(BillType billType);

    /**
     * 批量更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    Boolean updateBillTypes(List<BillType> billTypes);

    /**
     * 批量删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    Boolean deleteBillTypes(Integer[] billTypeIds);

}
