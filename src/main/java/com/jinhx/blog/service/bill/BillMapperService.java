package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.bill.Bill;

import java.util.List;

/**
 * BillService
 *
 * @author jinhx
 * @since 2021-07-28
 */
public interface BillMapperService extends IService<Bill> {

    /**
     * 查询单个账单信息
     *
     * @param billId billId
     * @return 单个账单信息
     */
    Bill getBill(Integer billId);

    /**
     * 查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    IPage<Bill> queryPage(Integer page, Integer limit, Boolean incomeExpenditureType);


    /**
     * 新增单个账单
     *
     * @param bill bill
     * @return 新增结果
     */
    Boolean insertBill(Bill bill);

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    Boolean insertBills(List<Bill> bills);

    /**
     * 更新单个账单
     *
     * @param bill bill
     * @return 更新结果
     */
    Boolean updateBill(Bill bill);

    /**
     * 批量更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    Boolean updateBills(List<Bill> bills);

    /**
     * 批量删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    Boolean deleteBills(Integer[] billIds);

}
