package com.jinhx.blog.service.file;

import com.jinhx.blog.common.config.CloudStorageProperties;
import com.jinhx.blog.entity.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * CloudStorageService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public abstract class CloudStorageService {

    /**
     * 云存储配置信息
     */
    protected CloudStorageProperties config;

    /**
     * 文件上传
     *
     * @param file file
     * @return fileModule fileModule
     * @return 返回http地址
     */
    public abstract FileVO upload(MultipartFile file, Integer fileModule);

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    /**
     * 文件上传
     *
     * @param data 文件字节数组
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix);

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param suffix 后缀
     * @return 返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix);

}
