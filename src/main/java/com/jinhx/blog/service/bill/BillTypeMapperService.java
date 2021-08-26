package com.jinhx.blog.service.bill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
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
     * 根据billTypeId查询账单类型
     *
     * @param billTypeId billTypeId
     * @return 账单类型
     */
    public BillType selectBillTypeById(Long billTypeId) {
        List<BillType> billTypes = selectBillTypesById(Lists.newArrayList(billTypeId));
        if (CollectionUtils.isEmpty(billTypes)){
            return null;
        }

        return billTypes.get(0);
    }

    /**
     * 根据billTypeId查询账单类型列表
     *
     * @param billTypeIds billTypeIds
     * @return 账单类型列表
     */
    public List<BillType> selectBillTypesById(List<Long> billTypeIds) {
        if (CollectionUtils.isEmpty(billTypeIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<BillType>().in(BillType::getBillTypeId, billTypeIds));
    }

    /**
     * 分页查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    public IPage<BillType> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return baseMapper.selectPage(new QueryPage<BillType>(page, limit).getPage(),
                new LambdaQueryWrapper<BillType>()
                        .eq(Objects.nonNull(incomeExpenditureType), BillType::getIncomeExpenditureType, incomeExpenditureType)
                        .orderByDesc(BillType::getCreateTime));
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBillTypes(List<BillType> billTypes) {
        if (CollectionUtils.isNotEmpty(billTypes)){
            if (billTypes.stream().mapToInt(item -> baseMapper.insert(item)).sum() != billTypes.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 批量根据billTypeId更新账单类型
     *
     * @param billTypes billTypes
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBillTypesById(List<BillType> billTypes) {
        if (CollectionUtils.isNotEmpty(billTypes)){
            if (billTypes.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != billTypes.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 批量根据billTypeId删除账单类型
     *
     * @param billTypeIds billTypeIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBillTypesById(List<Long> billTypeIds) {
        if (CollectionUtils.isNotEmpty(billTypeIds)){
            if (baseMapper.deleteBatchIds(billTypeIds) != billTypeIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

}
