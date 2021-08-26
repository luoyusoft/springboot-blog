package com.jinhx.blog.service.bill.impl;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.service.bill.BillMapperService;
import com.jinhx.blog.service.bill.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BillServiceImpl
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillMapperService billMapperService;

    /**
     * 根据billId查询账单
     *
     * @param billId billId
     * @return 账单
     */
    @Override
    public Bill selectBillById(Long billId) {
        return billMapperService.selectBillById(billId);
    }

    /**
     * 分页查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    @Override
    public PageData<Bill> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return new PageData<>(billMapperService.selectPage(page, limit, incomeExpenditureType));
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     */
    @Override
    public void insertBills(List<Bill> bills) {
        billMapperService.insertBills(bills);
    }

    /**
     * 批量根据billId更新账单
     *
     * @param bills bills
     */
    @Override
    public void updateBillsById(List<Bill> bills) {
        billMapperService.updateBillsById(bills);
    }

    /**
     * 批量根据billId删除账单
     *
     * @param billIds billIds
     */
    @Override
    public void deleteBillsById(List<Long> billIds) {
        billMapperService.deleteBillsById(billIds);
    }

}
