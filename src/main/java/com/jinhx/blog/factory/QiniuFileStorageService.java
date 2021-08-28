package com.jinhx.blog.factory;

import com.jinhx.blog.common.config.QiNiuProperties;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.JsonUtils;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * QiniuFileStorageService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class QiniuFileStorageService extends FileStorageService {

    @Autowired
    private QiNiuProperties qiNiuProperties;

    private UploadManager uploadManager;

    private String token;

    private Auth auth;

    /**
     * 初始化
     */
    @PostConstruct
    private void init(){
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        auth = Auth.create(qiNiuProperties.getQiniuAccessKey(), qiNiuProperties.getQiniuSecretKey());
        token = auth.uploadToken(qiNiuProperties.getQiniuBucketName());
    }

    /**
     * 文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return 返回http地址
     */
    @Override
    public String uploadByFileModule(MultipartFile file, Integer fileModule) {
        try {
            // todo 根据fileModule区分BucketName
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            return uploadBySuffix(file.getBytes(), suffix);
        }catch (Exception e){
            throw new MyException(ResponseEnums.OSS_UPLOAD_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 上传文件
     *
     * @param inputStream inputStream
     * @param objectName objectName
     * @param bucketName bucketName
     * @param contentType contentType
     */
    @Override
    public void upload(InputStream inputStream, String objectName, String bucketName, String contentType) {
        // todo 实现
    }

    /**
     * 获取文件url
     *
     * @param bucketName bucketName
     * @param objectName objectName
     * @return url
     */
    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        // todo 实现
        return null;
    }

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    private String uploadByPath(byte[] data, String path) {
        try {
            token = auth.uploadToken(qiNiuProperties.getQiniuBucketName());
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new MyException(ResponseEnums.OSS_UPLOAD_ERROR.getCode(), "上传七牛出错=" + JsonUtils.objectToJson(res));
            }
        } catch (Exception e) {
            throw new MyException(ResponseEnums.OSS_CONFIG_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }

        return qiNiuProperties.getQiniuDomain() + "/" + path;
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    @Override
    public String uploadByPath(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return uploadByPath(data, path);
        } catch (Exception e) {
            throw new MyException(ResponseEnums.OSS_CONFIG_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    private String uploadBySuffix(byte[] data, String suffix) {
        return uploadByPath(data, getPath(qiNiuProperties.getQiniuPrefix(), suffix));
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix 后缀
     * @return 返回http地址
     */
    @Override
    public String uploadBySuffix(InputStream inputStream, String suffix) {
        return uploadByPath(inputStream, getPath(qiNiuProperties.getQiniuPrefix(), suffix));
    }

    /**
     * 创建指定序号的分片文件上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param partNumber 分片序号
     * @param expiry 过期时间(秒) 最大为7天 超过7天则默认最大值
     * @return uploadChunkUrl
     */
    @Override
    public String createUploadChunkUrl(String bucketName, String objectMD5, Integer partNumber, Integer expiry) {
        // todo 实现
        return null;
    }

    /**
     * 批量创建分片上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param chunkCount 分片数量
     * @param expiry 过期时间(秒) 最大为7天 超过7天则默认最大值
     * @return uploadChunkUrls
     */
    @Override
    public List<String> createUploadChunkUrls(String bucketName, String objectMD5, Integer chunkCount, Integer expiry) {
        // todo 实现
        return null;
    }

    /**
     * 合并分片文件成对象文件
     *
     * @param chunkBucKetName 分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     */
    @Override
    public void composeChunkObject(String chunkBucKetName, String composeBucketName, List<String> chunkNames, String objectName) {
        // todo 实现
    }

    /**
     * 批量删除文件
     *
     * @param bucketName bucketName
     * @param objectNames 文件名列表
     */
    @Override
    public void deleteObjectNames(String bucketName, List<String> objectNames) {
        // todo 实现
    }

    /**
     * 下载文件
     *
     * @param response response
     * @param bucketName bucketName
     * @param objectName objectName
     */
    @Override
    public void download(HttpServletResponse response, String bucketName, String objectName) {
        // todo 实现
    }

    /**
     * 获取分片文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀（ObjectMd5）
     * @param sort 是否排序(升序)
     * @return 分片文件名称列表
     */
    @Override
    public List<String> getObjectNames(String bucketName, String prefix, Boolean sort) {
        // todo 实现
        return null;
    }

}
