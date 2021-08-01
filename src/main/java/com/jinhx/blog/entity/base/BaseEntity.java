package com.jinhx.blog.entity.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * BaseEntity
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
public class BaseEntity {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Id
    private Integer id;

    /**
     * 创建者id
     */
    @ApiModelProperty(value = "创建者id")
    @TableField(fill = FieldFill.INSERT)
    private Integer createrId;

    /**
     * 更新者id
     */
    @ApiModelProperty(value = "更新者id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updaterId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Field(type = FieldType.Date, format = DateFormat.none)
    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Field(type = FieldType.Date, format = DateFormat.none)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
