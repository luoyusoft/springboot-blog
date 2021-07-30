package com.jinhx.blog.mapper.file;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.file.File;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FileMapper
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface FileMapper extends BaseMapper<File> {

    /**
     * 根据文件md5，模块查询是否上传过
     *
     * @param fileMd5 fileMd5
     * @param module module
     * @return 文件信息
     */
    File selectFileByFileMd5AndModule(@Param("fileMd5") String fileMd5, @Param("module") Integer module);

    /**
     * 根据文件md5，模块更新状态
     *
     * @param file fileResource
     * @return 更新结果
     */
    Boolean updateFileByFileMd5AndModule(File file);

    /**
     * 批量查询文件
     *
     * @param ids ids
     * @return 文件列表
     */
    List<File> selectFileByIds(@Param("ids") Integer[] ids);

}
