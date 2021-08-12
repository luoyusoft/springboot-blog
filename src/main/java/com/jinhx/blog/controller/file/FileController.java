package com.jinhx.blog.controller.file;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.file.vo.FileVO;
import com.jinhx.blog.service.file.CloudStorageService;
import com.jinhx.blog.service.file.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.CollectionUtils;
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
    private CloudStorageService cloudStorageService;

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     *
     * @return fileVO fileVO
     * @return 返回http地址
     */
    @PostMapping("/manage/file/qiniuyun/upload")
    public Response uploadByQiNiuYun(FileVO fileVO) throws Exception {
        if (fileVO.getFile() == null || fileVO.getFile().isEmpty()
                || fileVO.getModule() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件，文件所属模块不能为空");
        }
        return Response.success(cloudStorageService.upload(fileVO.getFile(), fileVO.getModule()));
    }

    /**
     * 上传
     *
     * @param fileVO fileVO
     * @return FileVO
     */
    @PostMapping("/manage/file/minio/upload")
    public Response uploadByMinio(FileVO fileVO) throws Exception {
        if (fileVO.getFile() == null || fileVO.getFile().isEmpty()
                || fileVO.getModule() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件，文件所属模块不能为空");
        }
        return Response.success(fileService.upload(fileVO.getFile(), fileVO.getModule()));
    }

    /**
     * 分片上传文件
     *
     * @param fileVO fileVO
     * @return Response
     */
    @PostMapping("/manage/file/minio/chunkUpload")
    public Response chunkUpload(FileVO fileVO) throws Exception {
        if (fileVO.getFile() == null || fileVO.getFile().isEmpty()
                || StringUtils.isEmpty(fileVO.getBucketName()) || StringUtils.isEmpty(fileVO.getFileMd5())
                || fileVO.getChunkNumber() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "上传文件，桶名，文件的md5，分片序号不能为空");
        }
        fileService.chunkUpload(fileVO.getFile(), fileVO.getBucketName(), fileVO.getFileMd5(), fileVO.getChunkNumber());
        return Response.success();
    }

    /**
     * 下载文件
     *
     * @param response response
     * @param fileVO fileVO
     * @return Response
     */
    @PostMapping("/manage/file/minio/download")
    public Response downloadByMinio(HttpServletResponse response, @RequestBody FileVO fileVO) throws Exception {
        fileService.download(response, fileVO.getFileName());
        return Response.success();
    }

    /**
     * 分页查询文件
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @param fileName fileName
     * @param fileMd5 fileMd5
     * @param url url
     * @return PageUtils
     */
    @GetMapping("/manage/file/list")
    @RequiresPermissions("file:list")
    public Response listTimeline(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("module") Integer module,
                                 @RequestParam("fileName") String fileName, @RequestParam("fileMd5") String fileMd5, @RequestParam("url") String url) {
        PageData logViewPage = fileService.queryPage(page, limit, module, fileName, fileMd5, url);
        return Response.success(logViewPage);
    }

    /**
     * 分片上传文件，获取各个分片上传地址
     *
     * @param fileVO fileVO
     * @return List<FileVO>
     */
    @PostMapping("/manage/file/minio/chunk")
    public Response chunk(@RequestBody FileVO fileVO){
        if (StringUtils.isEmpty(fileVO.getFileMd5()) || StringUtils.isEmpty(fileVO.getFileName())
                || fileVO.getModule() == null || fileVO.getChunkCount() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文件md5，文件名称，文件所属模块，分片总数量不能为空");
        }
        return Response.success(fileService.chunk(fileVO));
    }

    /**
     * 分片上传，单个分片成功
     *
     * @param fileVO fileVO
     * @return Boolean
     */
    @PutMapping("/manage/file/minio/chunkUploadSuccess")
    public Response chunkUploadSuccess(@RequestBody FileVO fileVO){
        if (StringUtils.isEmpty(fileVO.getFileMd5()) || fileVO.getChunkNumber() == null
                || fileVO.getModule() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文件md5，当前分片，文件所属模块不能为空");
        }
        return Response.success(fileService.chunkUploadSuccess(fileVO));
    }

    /**
     * 合并文件并返回文件信息
     *
     * @param fileVO fileVO
     * @return String
     */
    @PostMapping("/manage/file/minio/compose")
    public Response composeFile(@RequestBody FileVO fileVO){
        if (StringUtils.isEmpty(fileVO.getFileMd5()) || StringUtils.isEmpty(fileVO.getFileName())
                || fileVO.getModule() == null || fileVO.getChunkCount() == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文件md5，文件名称，文件所属模块，分片总数量不能为空");
        }

        return Response.success(fileService.composeFile(fileVO));
    }

    /**
     * 获取文件访问地址
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return String
     */
    @GetMapping("/manage/file/minio/url")
    public Response getFileUrl(@RequestParam("fileMd5") String fileMd5, @RequestParam("module") Integer module){
        if (StringUtils.isEmpty(fileMd5) || module == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "文件md5，文件所属模块不能为空");
        }

        return Response.success(fileService.getFileUrl(fileMd5, module));
    }

    /**
     * 批量删除文件
     *
     * @param ids ids
     */
    @DeleteMapping("/manage/file/minio/file")
    @RequiresPermissions("file:delete")
    public Response deleteFile(@RequestBody List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能为空");
        }

        if (ids.size() > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能超过100个");
        }

        fileService.deleteFile(ids);
        return Response.success();
    }

}
