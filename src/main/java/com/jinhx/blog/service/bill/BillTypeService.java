package com.jinhx.blog.service.bill;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.BillType;

import java.util.List;

/**
 * BillTypeService
 *
 * @author jinhx
 * @since 2021-07-28
 */
public interface BillTypeService {

    /**
     * 根据billTypeId查询账单类型
     *
     * @param billTypeId billTypeId
     * @return 账单类型
     */
    BillType selectBillTypeById(Long billTypeId);

    /**
     * 分页查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    PageData<BillType> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType);

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     */
    void insertBillTypes(List<BillType> billTypes);

    /**
     * 批量根据billTypeId更新账单类型
     *
     * @param billTypes billTypes
     */
    void updateBillTypesById(List<BillType> billTypes);

    /**
     * 批量根据billTypeId删除账单类型
     *
     * @param billTypeIds billTypeIds
     */
    void deleteBillTypesById(List<Long> billTypeIds);

}
