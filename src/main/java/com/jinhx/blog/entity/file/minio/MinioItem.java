package com.jinhx.blog.entity.file.minio;

import io.minio.messages.Item;
import io.minio.messages.Owner;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MinioItem
 *
 * @author jinhx
 * @since 2018-11-30
 */
@Data
public class MinioItem {

    // 文件名称
    @ApiModelProperty("文件名称")
    private String objectName;

    // 最后操作时间
    @ApiModelProperty("最后操作时间")
    private LocalDateTime lastModified;

    private String etag;

    // 对象大小
    @ApiModelProperty("对象大小")
    private String size;

    private String storageClass;

    private Owner owner;

    // 对象类型：directory（目录）或file（文件）
    @ApiModelProperty("对象类型：directory（目录）或file（文件）")
    private String type;

    @ApiModelProperty("下载地址")
    private String url;

    public MinioItem() {
    }

    public MinioItem(Item item) {
        this.objectName = item.objectName();
        this.type = item.isDir() ? "directory" : "file";
        this.etag = item.etag();
        long sizeNum = item.size();
        this.size = sizeNum > 0 ? this.convertFileSize(sizeNum):"0";
        this.storageClass = item.storageClass();
        this.owner = item.owner();
        this.lastModified = item.lastModified().toLocalDateTime();
    }

    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else{
            return String.format("%d B", size);
        }
    }

}
