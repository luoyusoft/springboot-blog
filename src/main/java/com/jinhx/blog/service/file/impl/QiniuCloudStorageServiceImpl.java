package com.jinhx.blog.service.file.impl;

import com.jinhx.blog.common.config.CloudStorageProperties;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.DateUtils;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.entity.file.vo.FileVO;
import com.jinhx.blog.service.file.CloudStorageService;
import com.jinhx.blog.service.file.FileService;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * QiniuCloudStorageServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service("cloudStorageService")
@Slf4j
public class QiniuCloudStorageServiceImpl extends CloudStorageService {

    @Autowired
    private CloudStorageProperties cloudStorageProperties;

    @Autowired
    private FileService fileService;

    private UploadManager uploadManager;

    private String token;

    private Auth auth;

    /**
     * 初始化
     */
    public QiniuCloudStorageServiceImpl(CloudStorageProperties config){
        this.config = config;
        //初始化
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
        token = auth.uploadToken(config.getQiniuBucketName());
    }

    /**
     * 文件上传
     *
     * @param file file
     * @return fileModule fileModule
     * @return 返回http地址
     */
    @Override
    public FileVO upload(MultipartFile file, Integer fileModule) {
        try {
            //上传文件
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String url = uploadSuffix(file.getBytes(), suffix);
            File fileResource = new File();
            fileResource.setModule(fileModule);
            fileResource.setFileName(fileName);
            fileResource.setBucketName(cloudStorageProperties.getQiniuBucketName());
            fileResource.setStorageType(File.STORAGE_TYPE_QINIUYUN);
            fileResource.setUrl(url);
            fileService.save(fileResource);
            FileVO fileVO = new FileVO();
            fileVO.setFileName(fileName);
            fileVO.setUrl(url);
            return fileVO;
        }catch (Exception e){
            throw new MyException(ResponseEnums.OSS_UPLOAD_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    private String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;
        if(StringUtils.isNotBlank(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    @Override
    public String upload(byte[] data, String path) {
        try {
            token = auth.uploadToken(config.getQiniuBucketName());
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResponseEnums.OSS_CONFIG_ERROR.getCode(), e.getMessage());
        }

        return config.getQiniuDomain() + "/" + path;
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return upload(data, path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResponseEnums.OSS_CONFIG_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getQiniuPrefix(), suffix));
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix 后缀
     * @return 返回http地址
     */
    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getQiniuPrefix(), suffix));
    }

}
