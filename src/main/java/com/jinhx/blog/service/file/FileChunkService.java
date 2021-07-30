package com.jinhx.blog.service.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.file.FileChunk;

import java.util.List;

/**
 * FileChunkService
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface FileChunkService extends IService<FileChunk> {

    /**
     * 检查是否上传完所有分片
     *
     * @param fileMd5 fileMd5
     * @return 是否上传完所有分片
     */
    Boolean checkIsUploadAllChunkByFileMd5(String fileMd5);

    /**
     * 根据文件md5查询分片信息
     *
     * @param fileMd5 fileMd5
     * @return 分片信息列表
     */
    List<FileChunk> selectFileChunksByFileMd5(String fileMd5);

    /**
     * 根据文件md5和分片序号更新状态
     *
     * @param fileChunk fileResource
     * @return 更新结果
     */
    Boolean updateFileChunkByFileMd5AndChunkNumber(FileChunk fileChunk);

    /**
     * 新增分片
     *
     * @param fileChunk fileResource
     * @return 新增结果
     */
    Boolean insertFileChunk(FileChunk fileChunk);

}
