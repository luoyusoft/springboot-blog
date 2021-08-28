package com.jinhx.blog.entity.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * LogView
 *
 * @author jinhx
 * @since 2019-02-15
 */
@Data
@ApiModel(value="LogView对象", description="浏览日志")
@EqualsAndHashCode(callSuper = true)
@TableName("log_view")
public class LogView extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -6163019509159971632L;

    public static final Long CREATER_UPDATER_SYS_ID = -1L;

    public static final String CREATER_UPDATER_SYS_NAME = "sys";

    @ApiModelProperty(value = "浏览日志id主键")
    @TableId(type = IdType.INPUT)
    private Long logViewId;

    @ApiModelProperty(value = "浏览模块")
    private Integer module;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "请求头参数")
    private String headrParams;

    @ApiModelProperty(value = "执行时长(毫秒)")
    private Long time;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份")
    private String region;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "设备生产厂商")
    private String deviceManufacturer;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "操作系统的版本号")
    private String osVersion;

    @ApiModelProperty(value = "请求uri")
    private String uri;

    @ApiModelProperty(value = "请求类型（GET，POST，DELETE等）")
    private String requestType;

    @ApiModelProperty(value = "请求体参数")
    private String bodyParams;

    @ApiModelProperty(value = "响应结果")
    private String response;

}
