package com.jinhx.blog.service.file;

import cn.hutool.core.util.IdUtil;
import com.jinhx.blog.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * FileStorageService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public abstract class FileStorageService {

    /**
     * 获取带单位的文件大小
     *
     * @param size size
     * @return String
     */
    public String getFileSize(Long size) {
        double num = 1024;

        if (size < num){
            return size + "B";
        }
        if (size < Math.pow(num, 2)){
            return new DecimalFormat("0.00").format(size / num) + "K";
        }
        if (size < Math.pow(num, 3)){
            return new DecimalFormat("0.00").format(size / Math.pow(num, 2)) + "M";
        }
        if (size < Math.pow(num, 4)){
            return new DecimalFormat("0.00").format(size / Math.pow(num, 3)) + "G";
        }
        if (size < Math.pow(num, 5)){
            return new DecimalFormat("0.00").format(size / Math.pow(num, 4)) + "T";
        }
        return null;
    }

    /**
     * 获取文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        String uuid = IdUtil.simpleUUID();
        // 文件路径
        String path = DateUtils.getNowDateString("yyyyMMdd") + "/" + uuid;
        if(StringUtils.isNotBlank(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return 返回http地址
     */
    public abstract String uploadByFileModule(MultipartFile file, Integer fileModule);

    /**
     * 上传文件
     *
     * @param inputStream inputStream
     * @param objectName objectName
     * @param bucketName bucketName
     * @param contentType contentType
     */
    public abstract void upload(InputStream inputStream, String objectName, String bucketName, String contentType);

    /**
     * 获取文件url
     *
     * @param bucketName bucketName
     * @param objectName objectName
     * @return url
     */
    public abstract String getObjectUrl(String bucketName, String objectName);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String uploadByPath(InputStream inputStream, String path);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract String uploadBySuffix(InputStream inputStream, String suffix);

    /**
     * 创建指定序号的分片文件上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param partNumber 分片序号
     * @param expiry 过期时间(秒) 最大为7天 超过7天则默认最大值
     * @return uploadChunkUrl
     */
    public abstract String createUploadChunkUrl(String bucketName, String objectMD5, Integer partNumber, Integer expiry);

    /**
     * 批量创建分片上传外链
     *
     * @param bucketName 存储桶名称
     * @param objectMD5 欲上传分片文件主文件的MD5
     * @param chunkCount 分片数量
     * @param expiry 过期时间(秒) 最大为7天 超过7天则默认最大值
     * @return uploadChunkUrls
     */
    public abstract List<String> createUploadChunkUrls(String bucketName, String objectMD5, Integer chunkCount, Integer expiry);

    /**
     * 合并分片文件成对象文件
     *
     * @param chunkBucKetName 分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames 分片文件名称集合
     * @param objectName 合并后的对象文件名称
     */
    public abstract void composeChunkObject(String chunkBucKetName, String composeBucketName, List<String> chunkNames, String objectName);

    /**
     * 批量删除文件
     *
     * @param bucketName bucketName
     * @param objectNames 文件名列表
     */
    public abstract void deleteObjectNames(String bucketName, List<String> objectNames);

    /**
     * 下载文件
     *
     * @param response response
     * @param bucketName bucketName
     * @param objectName objectName
     */
    public abstract void download(HttpServletResponse response, String bucketName, String objectName);

    /**
     * 获取分片文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀（ObjectMd5）
     * @param sort 是否排序(升序)
     * @return 分片文件名称列表
     */
    public abstract List<String> getObjectNames(String bucketName, String prefix, Boolean sort);

}
