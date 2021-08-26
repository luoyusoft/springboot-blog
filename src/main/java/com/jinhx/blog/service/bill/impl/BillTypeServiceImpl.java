package com.jinhx.blog.service.bill.impl;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.service.bill.BillTypeMapperService;
import com.jinhx.blog.service.bill.BillTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BillTypeServiceImpl
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Service
public class BillTypeServiceImpl implements BillTypeService {

    @Autowired
    private BillTypeMapperService billTypeMapperService;

    /**
     * 根据billTypeId查询账单类型
     *
     * @param billTypeId billTypeId
     * @return 账单类型
     */
    @Override
    public BillType selectBillTypeById(Long billTypeId) {
        return billTypeMapperService.selectBillTypeById(billTypeId);
    }

    /**
     * 分页查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    @Override
    public PageData<BillType> selectPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        return new PageData<>(billTypeMapperService.selectPage(page, limit, incomeExpenditureType));
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     */
    @Override
    public void insertBillTypes(List<BillType> billTypes) {
        billTypeMapperService.insertBillTypes(billTypes);
    }

    /**
     * 批量根据billTypeId更新账单类型
     *
     * @param billTypes billTypes
     */
    @Override
    public void updateBillTypesById(List<BillType> billTypes) {
        billTypeMapperService.updateBillTypesById(billTypes);
    }

    /**
     * 批量根据billTypeId删除账单类型
     *
     * @param billTypeIds billTypeIds
     */
    @Override
    public void deleteBillTypesById(List<Long> billTypeIds) {
        billTypeMapperService.deleteBillTypesById(billTypeIds);
    }

}
