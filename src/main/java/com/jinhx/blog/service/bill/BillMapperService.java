package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
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
     * 根据billId查询账单
     *
     * @param billId billId
     * @return 账单
     */
    public Bill selectBillById(Long billId) {
        List<Bill> bills = selectBillsById(Lists.newArrayList(billId));
        if (CollectionUtils.isEmpty(bills)){
            return null;
        }

        return bills.get(0);
    }

    /**
     * 根据billId查询账单列表
     *
     * @param billIds billIds
     * @return 账单列表
     */
    public List<Bill> selectBillsById(List<Long> billIds) {
        if (CollectionUtils.isEmpty(billIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Bill>().in(Bill::getBillId, billIds));
    }

    /**
     * 分页查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    public IPage<Bill> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return baseMapper.selectPage(new QueryPage<Bill>(page, limit).getPage(),
                new LambdaQueryWrapper<Bill>()
                        .eq(Objects.nonNull(incomeExpenditureType), Bill::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(Bill::getDate));
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBills(List<Bill> bills) {
        if (CollectionUtils.isNotEmpty(bills)){
            if (bills.stream().mapToInt(item -> baseMapper.insert(item)).sum() != bills.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 批量根据billId更新账单
     *
     * @param bills bills
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBillsById(List<Bill> bills) {
        if (CollectionUtils.isNotEmpty(bills)){
            if (bills.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != bills.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 批量根据billId删除账单
     *
     * @param billIds billIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBillsById(List<Long> billIds) {
        if (CollectionUtils.isNotEmpty(billIds)){
            if (baseMapper.deleteBatchIds(billIds) != billIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

}
