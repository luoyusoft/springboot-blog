package com.jinhx.blog.service.file.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.config.MinioProperties;
import com.jinhx.blog.common.config.QiNiuProperties;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.entity.file.FileChunk;
import com.jinhx.blog.entity.file.vo.FileVO;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.file.*;
import com.jinhx.blog.service.operation.FriendLinkMapperService;
import com.jinhx.blog.service.video.VideoMapperService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * FileServiceImpl
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class FileServiceImpl implements FileService {
    
    @Autowired
    private QiNiuProperties qiNiuProperties;

    @Autowired
    private FileChunkMapperService fileChunkMapperService;

    @Autowired
    private FileMapperService fileMapperService;

    @Autowired
    private ArticleMapperService articleMapperService;

    @Autowired
    private VideoMapperService videoMapperService;

    @Autowired
    private FriendLinkMapperService friendLinkMapperService;

    @Autowired
    private FileStorageServiceFactory fileStorageServiceFactory;

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 七牛文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return FileVO
     */
    @Override
    public FileVO uploadByQiNiu(MultipartFile file, Integer fileModule) {
        try {
            String fileName = file.getOriginalFilename();
            String url = fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_QINIUYUN).uploadByFileModule(file, fileModule);
            File fileResource = new File();
            fileResource.setModule(fileModule);
            fileResource.setFileName(fileName);
            fileResource.setBucketName(qiNiuProperties.getQiniuBucketName());
            fileResource.setStorageType(File.STORAGE_TYPE_QINIUYUN);
            fileResource.setUrl(url);
            fileMapperService.insertFile(fileResource);
            FileVO fileVO = new FileVO();
            fileVO.setFileName(fileName);
            fileVO.setUrl(url);
            return fileVO;
        }catch (Exception e){
            throw new MyException(ResponseEnums.OSS_UPLOAD_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Minio文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return FileVO
     */
    @Override
    public FileVO uploadByMinio(MultipartFile file, Integer fileModule) {
        try {
            FileStorageService fileStorageService = fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO);
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            String patchName = fileStorageService.getPath("", suffix);
            String storageType = File.STORAGE_TYPE_MINIO;
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

            fileStorageService.upload(inputStream, patchName, bucketName, contentType);
            String url = fileStorageService.getObjectUrl(bucketName, patchName);
            url = url.replace(minioProperties.getMinioBaseUrl(), minioProperties.getMinioInitUrl()).substring(0, url.indexOf("?") + 1);
            fileResource.setFileName(fileName);
            fileResource.setBucketName(bucketName);
            fileResource.setStorageType(storageType);
            fileResource.setUrl(url);
            fileResource.setIsChunk(File.IS_CHUNK_FALSE);
            fileResource.setChunkCount(0);
            fileResource.setUploadStatus(File.UPLOAD_STATUS_1);
            fileResource.setSuffix(suffix);
            fileResource.setFileMd5(DigestUtils.md5Hex(file.getInputStream()));
            fileResource.setFileSize(fileStorageService.getFileSize(file.getSize()));
            fileMapperService.insertFile(fileResource);
            FileVO fileVO = new FileVO();
            fileVO.setFileName(fileName);
            fileVO.setUrl(url);
            return fileVO;
        }catch (Exception e){
            throw new MyException(ResponseEnums.MINIO_UPLOAD_ERROR.getCode(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Minio分片上传文件
     *
     * @param file file
     * @param bucketName bucketName
     * @param fileMd5 fileMd5
     * @param chunkNumber chunkNumber
     */
    @Override
    public void chunkUploadByMinio(MultipartFile file, String bucketName, String fileMd5, Integer chunkNumber) {
        try {
            fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO).upload(file.getInputStream(), fileMd5 + "/" + chunkNumber + ".chunk", bucketName, file.getContentType());
        }catch (Exception e){
            throw new MyException(ResponseEnums.MINIO_UPLOAD_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * Minio下载文件
     *
     * @param response response
     * @param fileName fileName
     */
    @Override
    public void downloadByMinio(HttpServletResponse response, String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String bucketName;
        if (suffix.equals(".mp4")){
            bucketName = File.BUCKET_NAME_VIDEO;
        }else if (suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".png")){
            bucketName = File.BUCKET_NAME_IMG;
        }else {
            bucketName = File.BUCKET_NAME_OTHER;
//            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "不存在该文件类型");
        }

        fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO).download(response, bucketName, fileName);
    }

    /**
     * 获取各个分片上传地址
     *
     * @param fileVO fileVO
     * @return List<FileVO>
     */
    @Override
    public List<FileVO> getUploadChunkFileVOsByMinio(FileVO fileVO) {
        String bucketName = null;
        File fileResource = fileMapperService.selectFileByFileMd5AndModuleAndIsChunk(fileVO.getFileMd5(), fileVO.getModule(), File.IS_CHUNK_TRUE);

        // 校验该文件是否上传过
        if(Objects.nonNull(fileResource)){
            // 秒传
            if(fileResource.getUploadStatus().equals(File.UPLOAD_STATUS_1)){
                return Collections.emptyList();
            }
            // 续传
            List<FileChunk> fileChunks = fileChunkMapperService.selectFileChunksByFileMd5(fileResource.getFileMd5());
            if (CollectionUtils.isNotEmpty(fileChunks)){
                List<FileVO> fileVOList = new ArrayList<>();
                for (FileChunk fileChunk : fileChunks){
                    FileVO file = new FileVO();
                    file.setUploadUrl(fileChunk.getUploadUrl());
                    file.setChunkNumber(fileChunk.getChunkNumber());
                    file.setUploadStatus(fileChunk.getUploadStatus());
                    file.setFileMd5(fileChunk.getFileMd5());
                    file.setBucketName(bucketName);
                    fileVOList.add(file);
                }

                return fileVOList;
            }
        }

        // 初次上传
        String suffix = fileVO.getFileName().substring(fileVO.getFileName().lastIndexOf("."));
        if (suffix.equals(".mp4")){
            bucketName = File.BUCKET_NAME_VIDEO;
        }else if (suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".png")){
            bucketName = File.BUCKET_NAME_IMG;
        }else {
            bucketName = File.BUCKET_NAME_OTHER;
//            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "暂不支持该文件格式");
        }

        FileStorageService fileStorageService = fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO);
        List<String> uploadUrls = fileStorageService.createUploadChunkUrls(bucketName, fileVO.getFileMd5(), fileVO.getChunkCount(), 604800);
        List<FileVO> fileVOList = new ArrayList<>();
        for (int i = 0; i < uploadUrls.size(); i++) {
            FileVO file = new FileVO();
            String url = fileStorageService.createUploadChunkUrl(bucketName, fileVO.getFileMd5(), i, 604800);
            file.setUploadUrl(url);
            file.setChunkNumber(i);
            file.setUploadStatus(FileChunk.UPLOAD_STATUS_0);
            file.setFileMd5(fileVO.getFileMd5());
            file.setBucketName(bucketName);
            fileVOList.add(file);

            FileChunk fileChunk = new FileChunk();
            fileChunk.setFileMd5(fileVO.getFileMd5());
            fileChunk.setUploadUrl(url);
            fileChunk.setUploadStatus(FileChunk.UPLOAD_STATUS_0);
            fileChunk.setChunkNumber(file.getChunkNumber());

            // 新增分片
            fileChunkMapperService.insertFileChunk(fileChunk);
        }

        // 向数据库中记录该文件的上传信息
        File newFile = new File();
        newFile.setFileName(fileVO.getFileName());
        newFile.setFileMd5(fileVO.getFileMd5());
        newFile.setBucketName(bucketName);
        newFile.setFileSize(fileStorageService.getFileSize(Long.valueOf(fileVO.getFileSize())));
        newFile.setIsChunk(File.IS_CHUNK_TRUE);
        newFile.setStorageType(File.STORAGE_TYPE_MINIO);
        newFile.setModule(fileVO.getModule());
        newFile.setSuffix(suffix);
        newFile.setChunkCount(fileVO.getChunkCount());
        newFile.setUploadStatus(File.UPLOAD_STATUS_0);
        fileMapperService.insertFile(newFile);

        return fileVOList;
    }

    /**
     * 更新单个分片上传成功
     *
     * @param fileVO fileVO
     */
    @Override
    public void updateChunkUploadSuccess(FileVO fileVO) {
        if (Objects.isNull(fileMapperService.selectFileByFileMd5AndModuleAndIsChunk(fileVO.getFileMd5(), fileVO.getModule(), File.IS_CHUNK_TRUE))){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该文件未上传过");
        }

        FileChunk fileChunk = new FileChunk();
        fileChunk.setFileMd5(fileVO.getFileMd5());
        fileChunk.setUploadStatus(FileChunk.UPLOAD_STATUS_1);
        fileChunk.setChunkNumber(fileVO.getChunkNumber());
        fileChunkMapperService.updateFileChunkByFileMd5AndChunkNumber(fileChunk);
    }

    /**
     * 合并文件并返回文件信息
     *
     * @param fileVO fileVO
     * @return 文件信息
     */
    @Override
    public String composeFileByMinio(FileVO fileVO) {
        if (Objects.isNull(fileMapperService.selectFileByFileMd5AndModuleAndIsChunk(fileVO.getFileMd5(), fileVO.getModule(), File.IS_CHUNK_TRUE))){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该文件未上传过");
        }

        if(fileChunkMapperService.selectFileChunkCountByFileMd5AndUploadStatus(fileVO.getFileMd5(), FileChunk.UPLOAD_STATUS_0) > 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该文件还有部分分片未上传");
        }

        String bucketName;
        String suffix = fileVO.getFileName().substring(fileVO.getFileName().lastIndexOf("."));
        if (suffix.equals(".mp4")){
            bucketName = File.BUCKET_NAME_VIDEO;
        }else if (suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".png")){
            bucketName = File.BUCKET_NAME_IMG;
        }else {
            bucketName = File.BUCKET_NAME_OTHER;
//            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "暂不支持该文件格式");
        }

        FileStorageService fileStorageService = fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO);
        // 根据md5获取所有分片文件名称(minio的文件名称 = 文件path)
        List<String> chunks = fileStorageService.getObjectNames(bucketName, fileVO.getFileMd5(), true);

        // 自定义文件名称
        String patchName = fileStorageService.getPath("", suffix);

        // 合并文件
        fileStorageService.composeChunkObject(bucketName, bucketName, chunks, patchName);
        fileStorageService.deleteObjectNames(bucketName, chunks);

        // 获取文件访问外链(1小时过期)
        String url = fileStorageService.getObjectUrl(bucketName, patchName);
        url = url.replace(minioProperties.getMinioBaseUrl(), minioProperties.getMinioInitUrl()).substring(0, url.indexOf("?") + 1);
        // 获取数据库里记录的文件信息，修改数据并返回文件信息
        File file = new File();
        file.setFileMd5(fileVO.getFileMd5());
        file.setModule(fileVO.getModule());
        file.setUrl(url);
        file.setUploadStatus(File.UPLOAD_STATUS_1);

        fileMapperService.updateFileByFileMd5AndModuleAndIsChunk(fileVO);

        return url;
    }

    /**
     * 分页查询文件列表
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @param fileName fileName
     * @param fileMd5 fileMd5
     * @param url url
     * @return 文件列表
     */
    @Override
    public PageData<File> selectPage(Integer page, Integer limit, Integer module, String fileName, String fileMd5, String url) {
        return fileMapperService.selectPage(page, limit, module, fileName, fileMd5, url);
    }

    /**
     * 获取文件访问地址
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return 文件访问地址
     */
    @Override
    public String getFileUrl(String fileMd5, Integer module) {
        File file = fileMapperService.selectFileByFileMd5AndModuleAndIsChunk(fileMd5, module, File.IS_CHUNK_TRUE);
        if (Objects.isNull(file)){
            return null;
        }

        return file.getUrl();
    }

    /**
     * 批量根据fileId删除文件
     *
     * @param fileIds fileIds
     */
    @Override
    public void deleteFilesById(List<Long> fileIds) {
        List<File> files = fileMapperService.selectFilesById(fileIds);

        List<Long> failList = new ArrayList<>();
        for (File filesItem : files) {
            // 检测文章
            if (articleMapperService.existByCover(filesItem.getUrl())){
                failList.add(filesItem.getFillId());
                continue;
            }

            // 检测视频
            if (videoMapperService.existByFile(filesItem.getUrl())){
                failList.add(filesItem.getFillId());
                continue;
            }

            String[] urls = filesItem.getUrl().split("/");
            fileStorageServiceFactory.getFileStorageService(File.STORAGE_TYPE_MINIO).deleteObjectNames(filesItem.getBucketName(),
                    Lists.newArrayList(DateTimeFormatter.ofPattern("yyyyMMdd").format(filesItem.getUpdateTime()) + "/" + urls[urls.length - 1]));
            fileMapperService.deleteFileById(filesItem.getFillId());
        }

        if (CollectionUtils.isNotEmpty(failList)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "部分文件已有关联，删除失败，列表：" + failList);
        }
    }

}
