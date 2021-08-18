package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.mapper.bill.BillMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * BillMapperService
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillMapperService extends ServiceImpl<BillMapper, Bill> {

    /**
     * 查询单个账单信息
     *
     * @param billId billId
     * @return 单个账单信息
     */
    public Bill getBill(Integer billId) {
        return baseMapper.selectById(billId);
    }

    /**
     * 查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    public IPage<Bill> queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return baseMapper.selectPage(new QueryPage<Bill>(page, limit).getPage(),
                new LambdaQueryWrapper<Bill>()
                        .eq(Objects.nonNull(incomeExpenditureType), Bill::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(Bill::getDate));
    }

    /**
     * 新增单个账单
     *
     * @param bill bill
     * @return 新增结果
     */
    public Boolean insertBill(Bill bill) {
        return baseMapper.insert(bill) > 0;
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    public Boolean insertBills(List<Bill> bills) {
        return bills.stream().mapToInt(item -> baseMapper.insert(item)).sum() == bills.size();
    }

    /**
     * 更新单个账单
     *
     * @param bill bill
     * @return 更新结果
     */
    public Boolean updateBill(Bill bill) {
        return baseMapper.updateById(bill) > 0;
    }

    /**
     * 批量更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    public Boolean updateBills(List<Bill> bills) {
        return bills.stream().mapToInt(item -> baseMapper.updateById(item)).sum() == bills.size();
    }

    /**
     * 批量删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBills(Integer[] billIds) {
        return baseMapper.deleteBatchIds(Lists.newArrayList(billIds)) == billIds.length;
    }

}
