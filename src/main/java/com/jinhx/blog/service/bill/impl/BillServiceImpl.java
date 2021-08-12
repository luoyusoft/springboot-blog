package com.jinhx.blog.service.bill.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.mapper.bill.BillMapper;
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
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private BillMapperService billMapperService;

    /**
     * 查询单个账单信息
     *
     * @param billId billId
     * @return 单个账单信息
     */
    @Override
    public Bill getBill(Integer billId) {
        return billMapperService.getBill(billId);
    }

    /**
     * 查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    @Override
    public PageData queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        IPage<Bill> billIPage = billMapperService.queryPage(page, limit, incomeExpenditureType);

        return new PageData(billIPage);
    }

    /**
     * 新增单个账单
     *
     * @param bill bill
     * @return 新增结果
     */
    @Override
    public Boolean insertBill(Bill bill) {
        return billMapperService.insertBill(bill);
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    @Override
    public Boolean insertBills(List<Bill> bills) {
        return billMapperService.insertBills(bills);
    }

    /**
     * 更新单个账单
     *
     * @param bill bill
     * @return 更新结果
     */
    @Override
    public Boolean updateBill(Bill bill) {
        return billMapperService.updateBill(bill);
    }

    /**
     * 批量更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    @Override
    public Boolean updateBills(List<Bill> bills) {
        return billMapperService.updateBills(bills);
    }

    /**
     * 批量删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    @Override
    public Boolean deleteBills(Integer[] billIds) {
        return billMapperService.deleteBills(billIds);
    }

}
