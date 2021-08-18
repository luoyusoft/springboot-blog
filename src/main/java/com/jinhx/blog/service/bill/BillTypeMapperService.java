package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.mapper.bill.BillTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * BillTypeMapperService
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillTypeMapperService extends ServiceImpl<BillTypeMapper, BillType> {

    /**
     * 查询单个账单类型信息
     *
     * @param billTypeId billTypeId
     * @return 单个账单类型信息
     */
    public BillType getBillType(Integer billTypeId) {
        return baseMapper.selectById(billTypeId);
    }

    /**
     * 查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    public IPage<BillType> queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return baseMapper.selectPage(new QueryPage<BillType>(page, limit).getPage(),
                new LambdaQueryWrapper<BillType>()
                        .eq(Objects.nonNull(incomeExpenditureType), BillType::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(BillType::getCreateTime));
    }

    /**
     * 新增单个账单类型
     *
     * @param billType billType
     * @return 新增结果
     */
    public Boolean insertBillType(BillType billType) {
        return baseMapper.insert(billType) > 0;
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    public Boolean insertBillTypes(List<BillType> billTypes) {
        return billTypes.stream().mapToInt(item -> baseMapper.insert(item)).sum() == billTypes.size();
    }

    /**
     * 更新单个账单类型
     *
     * @param billType billType
     * @return 更新结果
     */
    public Boolean updateBillType(BillType billType) {
        return baseMapper.updateById(billType) > 0;
    }

    /**
     * 批量更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    public Boolean updateBillTypes(List<BillType> billTypes) {
        return billTypes.stream().mapToInt(item -> baseMapper.updateById(item)).sum() == billTypes.size();
    }

    /**
     * 批量删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBillTypes(Integer[] billTypeIds) {
        return baseMapper.deleteBatchIds(Lists.newArrayList(billTypeIds)) == billTypeIds.length;
    }

}
