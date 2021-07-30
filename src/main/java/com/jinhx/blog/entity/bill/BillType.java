package com.jinhx.blog.entity.bill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * BillType
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Data
@ApiModel(value="BillType对象", description="账单类型")
@EqualsAndHashCode(callSuper = false)
@TableName("bill_type")
public class BillType extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "标题不能为空", groups = {AddGroup.class})
    @ApiModelProperty(value = "标题")
    private String title;

    @NotNull(message = "收入支出类型不能为空", groups = {AddGroup.class})
    @ApiModelProperty(value = "收入支出类型（0：支出，1：收入）")
    private Boolean incomeExpenditureType;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
