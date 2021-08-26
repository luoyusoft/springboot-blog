package com.jinhx.blog.service.file;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.file.File;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * FileStorageServiceFactory
 *
 * @author jinhx
 * @since 2021-08-21
 */
@Component
public class FileStorageServiceFactory {

    @Resource
    private FileStorageService minioFileStorageService;

    @Resource
    private FileStorageService qiniuFileStorageService;

    public FileStorageService getFileStorageService(String type){
        switch (type){
            case File.STORAGE_TYPE_MINIO:
                return minioFileStorageService;
            case File.STORAGE_TYPE_QINIUYUN:
                return qiniuFileStorageService;
            default:
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "不存在的文件存储类型");
        }
    }


}
