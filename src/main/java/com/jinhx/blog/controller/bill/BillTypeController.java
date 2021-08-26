package com.jinhx.blog.controller.bill;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.service.bill.BillTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BillTypeController
 *
 * @author jinhx
 * @since 2021-07-28
 */
@RestController
public class BillTypeController {

    @Autowired
    private BillTypeService billTypeService;

    /**
     * 根据billTypeId查询账单类型
     *
     * @param billTypeId billTypeId
     * @return 账单类型
     */
    @GetMapping("/manage/bill_type/{billId}")
    @RequiresPermissions("billType:info")
    public Response<BillType> selectBillTypesById(@PathVariable("billTypeId") Long billTypeId) {
        return Response.success(billTypeService.selectBillTypeById(billTypeId));
    }

    /**
     * 分页查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    @GetMapping("/manage/bill_types")
    @RequiresPermissions("billType:list")
    public Response<PageData<BillType>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("incomeExpenditureType") Boolean incomeExpenditureType) {
        return Response.success(billTypeService.selectPage(page, limit, incomeExpenditureType));
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    @PostMapping("/manage/bill_types")
    @RequiresPermissions("billType:insert")
    public Response<Void> insertBillTypes(@RequestBody List<BillType> billTypes){
        billTypes.forEach(item -> {
            ValidatorUtils.validateEntity(item, InsertGroup.class);
        });

        billTypeService.insertBillTypes(billTypes);

        return Response.success();
    }

    /**
     * 批量根据billTypeId更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    @PutMapping("/manage/bill_types")
    @RequiresPermissions("billType:update")
    public Response<Void> updateBillTypesById(@RequestBody List<BillType> billTypes){
        MyAssert.sizeBetween(billTypes, 1, 100, "billTypeIds");
        billTypeService.updateBillTypesById(billTypes);
        return Response.success();
    }

    /**
     * 批量根据billTypeId删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/bill_types")
    @RequiresPermissions("billType:delete")
    public Response<Void> deleteBillTypesById(@RequestBody List<Long> billTypeIds) {
        MyAssert.sizeBetween(billTypeIds, 1, 100, "billTypeIds");
        billTypeService.deleteBillTypesById(billTypeIds);
        return Response.success();
    }

}
