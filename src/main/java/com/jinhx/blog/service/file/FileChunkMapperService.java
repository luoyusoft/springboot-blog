package com.jinhx.blog.service.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.file.FileChunk;
import com.jinhx.blog.mapper.file.FileChunkMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * FileChunkMapperService
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class FileChunkMapperService extends ServiceImpl<FileChunkMapper, FileChunk> {

    /**
     * 根据文件的md5，上传状态查询数量
     *
     * @param fileMd5 fileMd5
     * @param uploadStatus uploadStatus
     * @return 数量
     */
    public Integer selectFileChunkCountByFileMd5AndUploadStatus(String fileMd5, Integer uploadStatus) {
        return baseMapper.selectCount(new LambdaQueryWrapper<FileChunk>()
                .eq(FileChunk::getFileMd5, fileMd5)
                .eq(FileChunk::getUploadStatus, uploadStatus));
    }

    /**
     * 根据文件的md5查询分片信息
     *
     * @param fileMd5 fileMd5
     * @return 分片信息列表
     */
    public List<FileChunk> selectFileChunksByFileMd5(String fileMd5) {
        return baseMapper.selectList(new LambdaQueryWrapper<FileChunk>()
                .eq(StringUtils.isNotBlank(fileMd5), FileChunk::getFileMd5, fileMd5)
                .orderByAsc(FileChunk::getChunkNumber));
    }

    /**
     * 根据文件的md5，分片序号更新文章
     *
     * @param fileChunk fileResource
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFileChunkByFileMd5AndChunkNumber(FileChunk fileChunk) {
        if (baseMapper.update(fileChunk, new LambdaUpdateWrapper<FileChunk>()
                .eq(StringUtils.isNotBlank(fileChunk.getFileMd5()), FileChunk::getFileMd5, fileChunk.getFileMd5())
                .eq(Objects.nonNull(fileChunk.getChunkNumber()), FileChunk::getChunkNumber, fileChunk.getChunkNumber())) != 1){
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 新增分片
     *
     * @param fileChunk fileChunk
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFileChunk(FileChunk fileChunk) {
        insertFileChunks(Lists.newArrayList(fileChunk));
    }

    /**
     * 批量新增分片
     *
     * @param fileChunks fileChunks
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFileChunks(List<FileChunk> fileChunks) {
        if (CollectionUtils.isNotEmpty(fileChunks)){
            if (fileChunks.stream().mapToInt(item -> baseMapper.insert(item)).sum() != fileChunks.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

}
