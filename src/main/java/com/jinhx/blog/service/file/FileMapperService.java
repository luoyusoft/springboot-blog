package com.jinhx.blog.service.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.mapper.file.FileMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * FileMapperService
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class FileMapperService extends ServiceImpl<FileMapper, File> {

    /**
     * 新增文件
     *
     * @param file 文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFile(File file) {
        insertFiles(Lists.newArrayList(file));
    }

    /**
     * 批量新增文件
     *
     * @param files 文件列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFiles(List<File> files) {
        if (CollectionUtils.isNotEmpty(files)){
            if (files.stream().mapToInt(item -> baseMapper.insert(item)).sum() == files.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
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
    public PageData<File> selectPage(Integer page, Integer limit, Integer module, String fileName, String fileMd5, String url) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<File>(page, limit).getPage(),
                new LambdaQueryWrapper<File>()
                        .eq(module != null, File::getModule, module)
                        .like(!StringUtils.isEmpty(fileName), File::getFileName, fileName)
                        .like(!StringUtils.isEmpty(fileMd5), File::getFileMd5, fileMd5)
                        .like(!StringUtils.isEmpty(url), File::getUrl, url)
                        .orderByDesc(File::getCreateTime)));
    }

    /**
     * 根据文件的md5，模块，是否分片上传查询文件
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @param isChunk isChunk
     * @return 文件
     */
    public File selectFileByFileMd5AndModuleAndIsChunk(String fileMd5, Integer module, Boolean isChunk) {
        List<File> files = baseMapper.selectList(new LambdaQueryWrapper<File>()
                .eq(File::getFileMd5, fileMd5)
                .eq(File::getModule, module)
                .eq(File::getIsChunk, isChunk)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(files)){
            return null;
        }

        return files.get(0);
    }

    /**
     * 根据文件的md5，模块，是否分片上传更新文件
     *
     * @param file file
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFileByFileMd5AndModuleAndIsChunk(File file) {
        if (baseMapper.update(file, new LambdaUpdateWrapper<File>()
                .eq(File::getFileMd5, file.getFileMd5())
                .eq(File::getModule, file.getModule())
                .eq(File::getIsChunk, File.IS_CHUNK_TRUE)) != 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 获取文件访问地址
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return String
     */
    public String getFileUrl(String fileMd5, Integer module) {
        File file = baseMapper.selectOne(new LambdaQueryWrapper<File>()
                .eq(StringUtils.isNotBlank(fileMd5), File::getFileMd5, fileMd5)
                .eq(Objects.nonNull(module), File::getModule, module)
                .eq(File::getIsChunk, File.IS_CHUNK_TRUE));
        if (Objects.isNull(file)){
            return null;
        }
        return file.getUrl();
    }

    /**
     * 根据fileId查询文件
     *
     * @param fileId fileId
     * @return 文件
     */
    public File selectBillTypeById(Long fileId) {
        List<File> files = selectFilesById(Lists.newArrayList(fileId));
        if (CollectionUtils.isEmpty(files)){
            return null;
        }

        return files.get(0);
    }

    /**
     * 根据fileId查询文件列表
     *
     * @param fileIds fileIds
     * @return 文件列表
     */
    public List<File> selectFilesById(List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<File>().in(File::getFillId, fileIds));
    }

    /**
     * 批量根据fileId删除文件
     *
     * @param fileId fileId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileById(Long fileId) {
        deleteFilesById(Lists.newArrayList(fileId));
    }

    /**
     * 批量根据fileId删除文件
     *
     * @param fileIds fileIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFilesById(List<Long> fileIds) {
        if (CollectionUtils.isNotEmpty(fileIds)){
            if (baseMapper.deleteBatchIds(fileIds) != fileIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }
}
