package com.jinhx.blog.service.file;

import com.jinhx.blog.common.config.MinioProperties;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.MinioUtils;
import com.jinhx.blog.entity.file.File;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * MinioFileStorageService
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class MinioFileStorageService extends FileStorageService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return FileVO
     */
    @Override
    public String uploadByFileModule(MultipartFile file, Integer fileModule) {
        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            String patchName = getPath("", suffix);
            String bucketName;
            if (suffix.equals(".mp4")){
                bucketName = File.BUCKET_NAME_VIDEO;
            }else if (suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".png")){
                bucketName = File.BUCKET_NAME_IMG;
            }else {
                bucketName = File.BUCKET_NAME_OTHER;
//                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "暂不支持该文件格式");
            }

            File fileResource = new File();
            fileResource.setModule(fileModule);

            upload(inputStream, patchName, bucketName, contentType);
            String url = getObjectUrl(bucketName, patchName);
            return url.replace(minioProperties.getMinioBaseUrl(), minioProperties.getMinioInitUrl()).substring(0, url.indexOf("?") + 1);
        }catch (Exception e){
            throw new MyException(ResponseEnums.MINIO_UPLOAD_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }
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
        // todo 实现
        return null;
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
        minioUtils.upload(inputStream, objectName, bucketName, contentType);
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
        return minioUtils.getObjectUrl(bucketName, objectName);
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
        // todo 实现
        return null;
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
    public String createUploadChunkUrl(String bucketName, String objectMD5, Integer partNumber, Integer expiry){
        return minioUtils.createUploadChunkUrl(bucketName, objectMD5, partNumber, expiry);
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
    public List<String> createUploadChunkUrls(String bucketName, String objectMD5, Integer chunkCount, Integer expiry){
        return minioUtils.createUploadChunkUrls(bucketName, objectMD5, chunkCount, expiry);
    }

    /**
     * 合并分片文件成对象文件
     *
     * @param chunkBucKetName 分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     */
    public void composeChunkObject(String chunkBucKetName, String composeBucketName, List<String> chunkNames, String objectName){
        minioUtils.composeChunkObject(chunkBucKetName, composeBucketName, chunkNames, objectName);
    }

    /**
     * 批量删除文件
     *
     * @param bucketName bucketName
     * @param objectNames 文件名列表
     */
    public void deleteObjectNames(String bucketName, List<String> objectNames) {
        minioUtils.deleteObjectNames(bucketName, objectNames);
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
        minioUtils.download(response, bucketName, objectName);
    }

    /**
     * 获取分片文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀（ObjectMd5）
     * @param sort 是否排序(升序)
     * @return 分片文件名称列表
     */
    public List<String> getObjectNames(String bucketName, String prefix, Boolean sort){
        return minioUtils.getObjectNames(bucketName, prefix, sort);
    }

}
