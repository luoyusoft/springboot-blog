package com.jinhx.blog.controller.file;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.entity.file.vo.FileVO;
import com.jinhx.blog.service.file.FileService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * FileController
 *
 * @author jinhx
 * @since 2018-11-30
 */
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 七牛文件上传
     *
     * @param fileVO fileVO
     * @return FileVO
     */
    @PostMapping("/manage/file/qiniuyun/upload")
    public Response<FileVO> uploadByQiNiu(FileVO fileVO) {
        MyAssert.notNull(fileVO.getFile(), "上传文件不能为空");
        MyAssert.notNull(fileVO.getModule(), "文件所属模块不能为空");
        if (fileVO.getFile().isEmpty()) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件不能为空");
        }
        return Response.success(fileService.uploadByQiNiu(fileVO.getFile(), fileVO.getModule()));
    }

    /**
     * Minio文件上传
     *
     * @param fileVO fileVO
     * @return FileVO
     */
    @PostMapping("/manage/file/minio/upload")
    public Response<FileVO> uploadByMinio(FileVO fileVO) {
        MyAssert.notNull(fileVO.getFile(), "上传文件不能为空");
        MyAssert.notNull(fileVO.getModule(), "文件所属模块不能为空");
        if (fileVO.getFile().isEmpty()) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件不能为空");
        }
        return Response.success(fileService.uploadByMinio(fileVO.getFile(), fileVO.getModule()));
    }

    /**
     * Minio分片上传文件
     *
     * @param fileVO fileVO
     * @return 上传结果
     */
    @PostMapping("/manage/file/minio/chunkUpload")
    public Response<Void> chunkUploadByMinio(FileVO fileVO) {
        MyAssert.notNull(fileVO.getChunkNumber(), "分片序号不能为空");
        MyAssert.notEmpty(fileVO.getFileMd5(), "文件的md5不能为空");
        MyAssert.notEmpty(fileVO.getBucketName(), "桶名不能为空");
        if (fileVO.getFile().isEmpty()) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件不能为空");
        }
        fileService.chunkUploadByMinio(fileVO.getFile(), fileVO.getBucketName(), fileVO.getFileMd5(), fileVO.getChunkNumber());
        return Response.success();
    }

    /**
     * Minio下载文件
     *
     * @param response response
     * @param fileVO fileVO
     * @return Response
     */
    @PostMapping("/manage/file/minio/download")
    public Response<Void> downloadByMinio(HttpServletResponse response, @RequestBody FileVO fileVO) {
        fileService.downloadByMinio(response, fileVO.getFileName());
        return Response.success();
    }

    /**
     * Minio获取各个分片上传地址
     *
     * @param fileVO fileVO
     * @return List<FileVO>
     */
    @PostMapping("/manage/file/minio/chunk")
    public Response<List<FileVO>> getUploadChunkFileVOsByMinio(@RequestBody FileVO fileVO){
        MyAssert.notNull(fileVO.getChunkCount(), "分片总数量不能为空");
        MyAssert.notEmpty(fileVO.getFileMd5(), "文件的md5不能为空");
        MyAssert.notEmpty(fileVO.getFileName(), "文件名称不能为空");
        MyAssert.notNull(fileVO.getModule(), "文件所属模块不能为空");
        return Response.success(fileService.getUploadChunkFileVOsByMinio(fileVO));
    }

    /**
     * 更新单个分片上传成功
     *
     * @param fileVO fileVO
     * @return 更新结果
     */
    @PutMapping("/manage/file/minio/chunkUploadSuccess")
    public Response<Void> updateChunkUploadSuccess(@RequestBody FileVO fileVO){
        MyAssert.notNull(fileVO.getChunkNumber(), "当前分片不能为空");
        MyAssert.notEmpty(fileVO.getFileMd5(), "文件的md5不能为空");
        MyAssert.notNull(fileVO.getModule(), "文件所属模块不能为空");
        fileService.updateChunkUploadSuccess(fileVO);
        return Response.success();
    }

    /**
     * 合并文件并返回文件信息
     *
     * @param fileVO fileVO
     * @return 文件信息
     */
    @PostMapping("/manage/file/minio/compose")
    public Response<String> composeFileByMinio(@RequestBody FileVO fileVO){
        MyAssert.notEmpty(fileVO.getFileName(), "文件名称不能为空");
        MyAssert.notEmpty(fileVO.getFileMd5(), "文件的md5不能为空");
        MyAssert.notNull(fileVO.getModule(), "文件所属模块不能为空");
        MyAssert.notNull(fileVO.getChunkCount(), "分片总数量不能为空");
        return Response.success(fileService.composeFileByMinio(fileVO));
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
    @GetMapping("/manage/file/list")
    @RequiresPermissions("file:list")
    public Response<PageData<File>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("module") Integer module,
                                               @RequestParam("fileName") String fileName, @RequestParam("fileMd5") String fileMd5, @RequestParam("url") String url) {
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");
        return Response.success(fileService.selectPage(page, limit, module, fileName, fileMd5, url));
    }

    /**
     * 获取文件访问地址
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return 文件访问地址
     */
    @GetMapping("/manage/file/minio/url")
    public Response<String> getFileUrl(@RequestParam("fileMd5") String fileMd5, @RequestParam("module") Integer module){
        MyAssert.notEmpty(fileMd5, "文件的md5不能为空");
        MyAssert.notNull(module, "文件所属模块不能为空");
        return Response.success(fileService.getFileUrl(fileMd5, module));
    }

    /**
     * 批量根据fileId删除文件
     *
     * @param fileIds fileIds
     * @return  删除结果
     */
    @DeleteMapping("/manage/file/minio/file")
    @RequiresPermissions("file:delete")
    public Response<Void> deleteFilesById(@RequestBody List<Long> fileIds){
        MyAssert.sizeBetween(fileIds, 1, 100, "fileIds");
        fileService.deleteFilesById(fileIds);
        return Response.success();
    }

}
