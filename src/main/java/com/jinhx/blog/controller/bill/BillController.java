package com.jinhx.blog.controller.bill;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.service.bill.BillService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BillController
 *
 * @author jinhx
 * @since 2021-07-28
 */
@RestController
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * 根据billId查询账单
     *
     * @param billId billId
     * @return 根据billId查询账单
     */
    @GetMapping("/manage/bill/{billId}")
    @RequiresPermissions("bill:info")
    public Response<Bill> selectBillById(@PathVariable Long billId) {
        return Response.success(billService.selectBillById(billId));
    }

    /**
     * 分页查询账单列表
     *
     * @param page page
     * @param limit limit
     * @param incomeExpenditureType incomeExpenditureType
     * @return 账单列表
     */
    @GetMapping("/manage/bills")
    @RequiresPermissions("bill:list")
    public Response<PageData<Bill>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("incomeExpenditureType") Boolean incomeExpenditureType) {
        return Response.success(billService.selectPage(page, limit, incomeExpenditureType));
    }

    /**
     * 批量新增账单
     *
     * @param bills bills
     * @return 新增结果
     */
    @PostMapping("/manage/bills")
    @RequiresPermissions("bill:insert")
    public Response<Void> insertBills(@RequestBody List<Bill> bills){
        bills.forEach(item -> {
            ValidatorUtils.validateEntity(item, InsertGroup.class);
        });

        billService.insertBills(bills);

        return Response.success();
    }

    /**
     * 批量根据billId更新账单
     *
     * @param bills bills
     * @return 更新结果
     */
    @PutMapping("/manage/bills")
    @RequiresPermissions("bill:update")
    public Response<Void> updateBillsById(@RequestBody List<Bill> bills){
        MyAssert.sizeBetween(bills, 1, 100, "bills");
        billService.updateBillsById(bills);
        return Response.success();
    }

    /**
     * 批量根据billId删除账单
     *
     * @param billIds billIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/bill/delete")
    @RequiresPermissions("bill:delete")
    public Response<Void> deleteBillsById(@RequestBody List<Long> billIds) {
        MyAssert.sizeBetween(billIds, 1, 100, "billIds");
        billService.deleteBillsById(billIds);
        return Response.success();
    }

}
