package com.jinhx.blog.service.bill;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.Bill;

import java.util.List;

/**
 * BillService
 *
 * @author jinhx
 * @since 2021-07-28
 */
public interface BillService {

    /**
     * 根据billId查询账单
     *
     * @param billId billId
     * @return 账单
     */
    Bill selectBillById(Long billId);

    /**
     * 分页查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    PageData<Bill> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType);

    /**
     * 批量新增账单
     *
     * @param bills bills
     */
    void insertBills(List<Bill> bills);

    /**
     * 批量根据billId更新账单
     *
     * @param bills bills
     */
    void updateBillsById(List<Bill> bills);

    /**
     * 批量根据billId删除账单
     *
     * @param billIds billIds
     */
    void deleteBillsById(List<Long> billIds);

}
