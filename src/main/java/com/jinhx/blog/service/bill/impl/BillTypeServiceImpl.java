package com.jinhx.blog.service.bill.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.mapper.bill.BillTypeMapper;
import com.jinhx.blog.service.bill.BillTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BillTypeServiceImpl
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillTypeServiceImpl extends ServiceImpl<BillTypeMapper, BillType> implements BillTypeService {

    /**
     * 查询单个账单类型信息
     *
     * @param billTypeId billTypeId
     * @return 单个账单类型信息
     */
    @Override
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
    @Override
    public PageUtils queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("incomeExpenditureType", incomeExpenditureType);


        IPage<BillType> billPage = baseMapper.selectPage(new Query<BillType>(params).getPage(),
                new LambdaQueryWrapper<BillType>()
                        .eq(ObjectUtil.isNotNull(incomeExpenditureType), BillType::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(BillType::getCreateTime));

        return new PageUtils(billPage);
    }

    /**
     * 新增单个账单类型
     *
     * @param billType billType
     * @return 新增结果
     */
    @Override
    public Boolean insertBillType(BillType billType) {
        return baseMapper.insert(billType) > 0;
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    @Override
    public Boolean insertBillTypes(List<BillType> billTypes) {
        return billTypes.stream().mapToInt(item -> baseMapper.insert(item)).sum() == billTypes.size();
    }

    /**
     * 更新单个账单类型
     *
     * @param billType billType
     * @return 更新结果
     */
    @Override
    public Boolean updateBillType(BillType billType) {
        return baseMapper.updateById(billType) > 0;
    }

    /**
     * 批量更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    @Override
    public Boolean updateBillTypes(List<BillType> billTypes) {
        return billTypes.stream().mapToInt(item -> baseMapper.updateById(item)).sum() == billTypes.size();
    }

    /**
     * 批量删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBillTypes(Integer[] billTypeIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(billTypeIds)) == billTypeIds.length;
    }

}
