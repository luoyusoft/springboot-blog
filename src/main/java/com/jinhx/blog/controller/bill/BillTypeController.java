package com.jinhx.blog.controller.bill;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.service.bill.BillTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * BillTypeController
 *
 * @author jinhx
 * @since 2021-07-28
 */
@RestController
public class BillTypeController {

    @Resource
    private BillTypeService billTypeService;

    /**
     * 查询单个账单类型信息
     *
     * @param billTypeId billTypeId
     * @return 单个账单类型信息
     */
    @GetMapping("/manage/bill_type/{billId}")
    @RequiresPermissions("billType:info")
    public Response getBillType(@PathVariable("billTypeId") Integer billTypeId) {
        return Response.success(billTypeService.getBillType(billTypeId));
    }

    /**
     * 查询账单类型列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单类型列表
     */
    @GetMapping("/manage/bill_types")
    @RequiresPermissions("billType:list")
    public Response listBillTypes(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("incomeExpenditureType") Boolean incomeExpenditureType) {
        return Response.success(billTypeService.queryPage(page, limit, incomeExpenditureType));
    }

    /**
     * 新增单个账单类型
     *
     * @param billType billType
     * @return 新增结果
     */
    @PostMapping("/manage/bill_type")
    @RequiresPermissions("billType:insert")
    public Response insertBillType(@RequestBody BillType billType){
        ValidatorUtils.validateEntity(billType, AddGroup.class);
        Boolean result = billTypeService.insertBillType(billType);

        return Response.success(result);
    }

    /**
     * 批量新增账单类型
     *
     * @param billTypes billTypes
     * @return 新增结果
     */
    @PostMapping("/manage/bill_types")
    @RequiresPermissions("billType:insert")
    public Response insertBillTypes(@RequestBody List<BillType> billTypes){
        billTypes.forEach(item -> {
            ValidatorUtils.validateEntity(item, AddGroup.class);
        });
        Boolean result = billTypeService.insertBillTypes(billTypes);

        return Response.success(result);
    }

    /**
     * 更新单个账单类型
     *
     * @param billType billType
     * @return 更新结果
     */
    @PutMapping("/manage/bill_type")
    @RequiresPermissions("billType:update")
    public Response updateBillType(@RequestBody BillType billType){
        Boolean result = billTypeService.updateBillType(billType);
        return Response.success(result);
    }

    /**
     * 批量更新账单类型
     *
     * @param billTypes billTypes
     * @return 更新结果
     */
    @PutMapping("/manage/bill_types")
    @RequiresPermissions("billType:update")
    public Response updateBillTypes(@RequestBody List<BillType> billTypes){
        Boolean result = billTypeService.updateBillTypes(billTypes);
        return Response.success(result);
    }

    /**
     * 批量删除账单类型
     *
     * @param billTypeIds billTypeIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/bill_types")
    @RequiresPermissions("billType:delete")
    public Response deleteBillTypes(@RequestBody Integer[] billTypeIds) {
        if (billTypeIds == null || billTypeIds.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "billTypeIds不能为空");
        }

        if (billTypeIds.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "billTypeIds不能超过100个");
        }

        billTypeService.deleteBillTypes(billTypeIds);
        return Response.success();
    }

}
