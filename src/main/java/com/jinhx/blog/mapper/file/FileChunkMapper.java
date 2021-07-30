package com.jinhx.blog.mapper.file;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.file.FileChunk;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FileChunkMapper
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface FileChunkMapper extends BaseMapper<FileChunk> {

    /**
     * 检查是否上传完所有分片
     *
     * @param fileMd5 fileMd5
     * @param uploadStatus uploadStatus
     * @return
     */
    Integer checkIsUploadAllChunkByFileMd5(@Param("fileMd5") String fileMd5, @Param("uploadStatus") Integer uploadStatus);

    /**
     * 根据文件md5查询分片信息
     *
     * @param fileMd5 fileMd5
     * @return
     */
    List<FileChunk> selectFileChunksByFileMd5(@Param("fileMd5") String fileMd5);

    /**
     * 根据文件md5和分片序号更新状态
     *
     * @param fileChunk fileResource
     * @return
     */
    Boolean updateFileChunkByFileMd5AndChunkNumber(FileChunk fileChunk);

}
