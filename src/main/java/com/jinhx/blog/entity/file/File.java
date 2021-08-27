package com.jinhx.blog.entity.file;

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
 * File
 *
 * @author jinhx
 * @since 2018-11-30
 */
@Data
@ApiModel(value="File对象", description="文件表")
@EqualsAndHashCode(callSuper = true)
@TableName("file")
public class File extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4614708271763079532L;

    public static final String BUCKET_NAME_IMG = "img";

    public static final String BUCKET_NAME_VIDEO = "video";

    public static final String BUCKET_NAME_OTHER = "other";

    public static final String STORAGE_TYPE_QINIUYUN = "qiniuyun";

    public static final String STORAGE_TYPE_MINIO = "minio";

    public static final Integer UPLOAD_STATUS_0 = 0;

    public static final Integer UPLOAD_STATUS_1 = 1;

    public static final Boolean IS_CHUNK_FALSE = false;

    public static final Boolean IS_CHUNK_TRUE = true;

    /**
     * 主键
     */
    @ApiModelProperty(value = "文件id主键")
    @TableId(type = IdType.INPUT)
    private Long fileId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "url地址")
    private String url;

    @ApiModelProperty(value = "存储类型（qiniuyun，minio）")
    private String storageType;

    @ApiModelProperty(value = "桶名")
    private String bucketName;

    @ApiModelProperty(value = "文件所属模块（article，video，link）")
    private Integer module;

    @ApiModelProperty(value = "文件的md5")
    private String fileMd5;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "文件格式")
    private String suffix;

    @ApiModelProperty(value = "是否分片（0：否，1：是）")
    private Boolean isChunk;

    @ApiModelProperty(value = "分片总数量")
    private Integer chunkCount;

    @ApiModelProperty(value = "上传状态（0：部分成功，1：成功）")
    private Integer uploadStatus;

}
