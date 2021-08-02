package com.jinhx.blog.service.file.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.file.FileChunk;
import com.jinhx.blog.mapper.file.FileChunkMapper;
import com.jinhx.blog.service.file.FileChunkMapperService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FileChunkServiceImpl
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class FileChunkMapperServiceImpl extends ServiceImpl<FileChunkMapper, FileChunk> implements FileChunkMapperService {

    /**
     * 检查是否上传完所有分片
     *
     * @param fileMd5 fileMd5
     * @return 是否上传完所有分片
     */
    @Override
    public Boolean checkIsUploadAllChunkByFileMd5(String fileMd5) {
        return baseMapper.selectCount(new LambdaQueryWrapper<FileChunk>()
                .eq(ObjectUtil.isNotNull(fileMd5), FileChunk::getFileMd5, fileMd5)
                .eq(FileChunk::getFileMd5, FileChunk.UPLOAD_STATUS_0)) < 1;
    }

    /**
     * 根据文件md5查询分片信息
     *
     * @param fileMd5 fileMd5
     * @return 分片信息列表
     */
    @Override
    public List<FileChunk> selectFileChunksByFileMd5(String fileMd5) {
        return baseMapper.selectList(new LambdaQueryWrapper<FileChunk>()
                .eq(ObjectUtil.isNotNull(fileMd5), FileChunk::getFileMd5, fileMd5)
                .orderByAsc(FileChunk::getChunkNumber));
    }

    /**
     * 根据文件md5和分片序号更新状态
     *
     * @param fileChunk fileResource
     * @return 更新结果
     */
    @Override
    public Boolean updateFileChunkByFileMd5AndChunkNumber(FileChunk fileChunk) {
        return baseMapper.update(fileChunk, new LambdaUpdateWrapper<FileChunk>()
                .eq(ObjectUtil.isNotNull(fileChunk.getFileMd5()), FileChunk::getFileMd5, fileChunk.getFileMd5())
                .eq(ObjectUtil.isNotNull(fileChunk.getChunkNumber()), FileChunk::getChunkNumber, fileChunk.getChunkNumber())) > 0;
    }

    /**
     * 新增分片
     *
     * @param fileChunk fileResource
     * @return 新增结果
     */
    @Override
    public Boolean insertFileChunk(FileChunk fileChunk) {
        return baseMapper.insert(fileChunk) > 0;
    }

}
