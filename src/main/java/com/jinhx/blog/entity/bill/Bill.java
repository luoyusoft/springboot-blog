package com.jinhx.blog.entity.bill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Bill
 *
 * @author jinhx
 * @since 2021-07-28
 */
@Data
@ApiModel(value="Bill对象", description="收入支出账单")
@EqualsAndHashCode(callSuper = true)
@TableName("bill")
public class Bill extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1370579136074196212L;

    @ApiModelProperty(value = "账单id主键")
    @TableId(type = IdType.INPUT)
    private Long billId;

    @NotNull(message = "账单类型不能为空", groups = {InsertGroup.class})
    @ApiModelProperty(value = "账单类型id")
    private Long billTypeId;

    @NotNull(message = "收入支出类型不能为空", groups = {InsertGroup.class})
    @ApiModelProperty(value = "收入支出类型（0：支出，1：收入）")
    private Boolean incomeExpenditureType;

    @NotNull(message = "数额不能为空", groups = {InsertGroup.class})
    @ApiModelProperty(value = "数额（单位：RMB，分）")
    private Long amount;

    @ApiModelProperty(value = "日期")
    @Field(type = FieldType.Date, format = DateFormat.none)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @AllArgsConstructor
    @Getter
    public enum IncomeExpenditureTypeEnum {

        ExpenditureT(0, "支出"),
        Income(1, "收入");

        private Integer code;
        private String msg;

    }

}
