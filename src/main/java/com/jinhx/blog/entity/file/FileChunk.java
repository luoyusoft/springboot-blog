package com.jinhx.blog.entity.file;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * FileChunk
 *
 * @author jinhx
 * @since 2018-11-30
 */
@Data
@ApiModel(value="FileChunk对象", description="云存储分片表")
@EqualsAndHashCode(callSuper = false)
@TableName("file_chunk")
public class FileChunk extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -9032683187422485782L;

    public static final Integer UPLOAD_STATUS_0 = 0;

    public static final Integer UPLOAD_STATUS_1 = 1;

    @ApiModelProperty(value = "上传url地址")
    private String uploadUrl;

    @ApiModelProperty(value = "文件的md5")
    private String fileMd5;

    @ApiModelProperty(value = "上传状态（0：未上传，1：已上传）")
    private Integer uploadStatus;

    @ApiModelProperty(value = "分片序号")
    private Integer chunkNumber;

}
