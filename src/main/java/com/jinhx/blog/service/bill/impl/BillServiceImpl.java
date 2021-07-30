package com.jinhx.blog.service.bill.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.mapper.bill.BillMapper;
import com.jinhx.blog.service.bill.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BillServiceImpl
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    /**
     * 查询单个账单信息
     *
     * @param billId billId
     * @return 单个账单信息
     */
    @Override
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
    @Override
    public PageUtils queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("incomeExpenditureType", incomeExpenditureType);


        IPage<Bill> billPage = baseMapper.selectPage(new Query<Bill>(params).getPage(),
                new LambdaQueryWrapper<Bill>()
                        .eq(ObjectUtil.isNotNull(incomeExpenditureType), Bill::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(Bill::getDate));

        return new PageUtils(billPage);
    }

    /**
     * 新增单个账单
     *
     * @param bill bill
     * @return 新增结果
     */
    @Override
    public Boolean insertBill(Bill bill) {
        return baseMapper.insert(bill) > 0;
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    @Override
    public Boolean insertBills(List<Bill> bills) {
        return bills.stream().mapToInt(item -> baseMapper.insert(item)).sum() == bills.size();
    }

    /**
     * 更新单个账单
     *
     * @param bill bill
     * @return 更新结果
     */
    @Override
    public Boolean updateBill(Bill bill) {
        return baseMapper.updateById(bill) > 0;
    }

    /**
     * 批量更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    @Override
    public Boolean updateBills(List<Bill> bills) {
        return bills.stream().mapToInt(item -> baseMapper.updateById(item)).sum() == bills.size();
    }

    /**
     * 批量删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBills(Integer[] billIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(billIds)) == billIds.length;
    }

}
