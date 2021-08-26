package com.jinhx.blog.service.file;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.entity.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * FileService
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface FileService {

    /**
     * 上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return FileVO
     */
    FileVO uploadByQiNiu(MultipartFile file, Integer fileModule);

    /**
     * Minio文件上传
     *
     * @param file file
     * @param fileModule fileModule
     * @return FileVO
     */
    FileVO uploadByMinio(MultipartFile file, Integer fileModule);

    /**
     * Minio分片上传文件
     *
     * @param file file
     * @param bucketName bucketName
     * @param fileMd5 fileMd5
     * @param chunkNumber chunkNumber
     */
    void chunkUploadByMinio(MultipartFile file, String bucketName, String fileMd5, Integer chunkNumber);

    /**
     * Minio下载文件
     *
     * @param response response
     * @param fileName fileName
     */
    void downloadByMinio(HttpServletResponse response, String fileName);

    /**
     * 获取各个分片上传地址
     *
     * @param fileVO fileVO
     * @return List<FileVO>
     */
    List<FileVO> getUploadChunkFileVOsByMinio(FileVO fileVO);

    /**
     * 更新单个分片上传成功
     *
     * @param fileVO fileVO
     */
    void updateChunkUploadSuccess(FileVO fileVO);

    /**
     * 合并文件并返回文件信息
     *
     * @param fileVO fileVO
     * @return 文件信息
     */
    String composeFileByMinio(FileVO fileVO);

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
    PageData<File> selectPage(Integer page, Integer limit, Integer module, String fileName, String fileMd5, String url);

    /**
     * 获取文件访问地址
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return 文件访问地址
     */
    String getFileUrl(String fileMd5, Integer module);

    /**
     * 批量根据fileId删除文件
     *
     * @param fileIds fileIds
     */
    void deleteFilesById(List<Long> fileIds);

}
