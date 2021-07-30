package com.jinhx.blog.controller.bill;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.service.bill.BillService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * IncomeExpenditureBillController
 *
 * @author jinhx
 * @since 2021-07-28
 */
@RestController
public class BillController {

    @Resource
    private BillService billService;

    /**
     * 查询单个账单信息
     *
     * @param billId billId
     * @return 单个账单信息
     */
    @GetMapping("/manage/bill/{billId}")
    @RequiresPermissions("bill:info")
    public Response getBill(@PathVariable("billId") Integer billId) {
        return Response.success(billService.getBill(billId));
    }

    /**
     * 查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    @GetMapping("/manage/bills")
    @RequiresPermissions("bill:list")
    public Response listBills(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("incomeExpenditureType") Boolean incomeExpenditureType) {
        return Response.success(billService.queryPage(page, limit, incomeExpenditureType));
    }

    /**
     * 新增单个账单
     *
     * @param bill bill
     * @return 新增结果
     */
    @PostMapping("/manage/bill")
    @RequiresPermissions("bill:insert")
    public Response insertBill(@RequestBody Bill bill){
        ValidatorUtils.validateEntity(bill, AddGroup.class);
        Boolean result = billService.insertBill(bill);

        return Response.success(result);
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    @PostMapping("/manage/bills")
    @RequiresPermissions("bill:insert")
    public Response insertBill(@RequestBody List<Bill> bills){
        bills.forEach(item -> {
            ValidatorUtils.validateEntity(item, AddGroup.class);
        });
        Boolean result = billService.insertBills(bills);

        return Response.success(result);
    }

    /**
     * 更新单个账单
     *
     * @param bill bill
     * @return 更新结果
     */
    @PutMapping("/manage/bill")
    @RequiresPermissions("bill:update")
    public Response updateBill(@RequestBody Bill bill){
        Boolean result = billService.updateBill(bill);
        return Response.success(result);
    }

    /**
     * 批量更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    @PutMapping("/manage/bills")
    @RequiresPermissions("bill:update")
    public Response updateBill(@RequestBody List<Bill> bills){
        Boolean result = billService.updateBills(bills);
        return Response.success(result);
    }

    /**
     * 批量删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/bill/delete")
    @RequiresPermissions("bill:delete")
    public Response deleteBills(@RequestBody Integer[] billIds) {
        if (billIds == null || billIds.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "billIds不能为空");
        }

        if (billIds.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "billIds不能超过100个");
        }

        billService.deleteBills(billIds);
        return Response.success();
    }

}
