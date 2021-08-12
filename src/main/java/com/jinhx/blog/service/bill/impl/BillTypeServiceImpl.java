package com.jinhx.blog.service.bill.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.mapper.bill.BillTypeMapper;
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
public class BillTypeServiceImpl extends ServiceImpl<BillTypeMapper, BillType> implements BillTypeService {

    @Autowired
    private BillTypeMapperService billTypeMapperService;

    /**
     * 查询单个账单类型信息
     *
     * @param billTypeId billTypeId
     * @return 单个账单类型信息
     */
    @Override
    public BillType getBillType(Integer billTypeId) {
        return billTypeMapperService.getBillType(billTypeId);
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
    public PageData queryPage(Integer page, Integer limit, Boolean incomeExpenditureType) {
        IPage<BillType> billIPage = billTypeMapperService.queryPage(page, limit, incomeExpenditureType);

        return new PageData(billIPage);
    }

    /**
     * 新增单个账单类型
     *
     * @param billType billType
     * @return 新增结果
     */
    @Override
    public Boolean insertBillType(BillType billType) {
        return billTypeMapperService.insertBillType(billType);
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    @Override
    public Boolean insertBillTypes(List<BillType> billTypes) {
        return billTypeMapperService.insertBillTypes(billTypes);
    }

    /**
     * 更新单个账单类型
     *
     * @param billType billType
     * @return 更新结果
     */
    @Override
    public Boolean updateBillType(BillType billType) {
        return billTypeMapperService.updateBillType(billType);
    }

    /**
     * 批量更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    @Override
    public Boolean updateBillTypes(List<BillType> billTypes) {
        return billTypeMapperService.updateBillTypes(billTypes);
    }

    /**
     * 批量删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    @Override
    public Boolean deleteBillTypes(Integer[] billTypeIds) {
        return billTypeMapperService.deleteBillTypes(billTypeIds);
    }

}
