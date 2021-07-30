package com.jinhx.blog.mapper.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.log.LogView;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LogViewMapper
 *
 * @author jinhx
 * @since 2019-02-15
 */
public interface LogViewMapper extends BaseMapper<LogView> {

    /*
     * 获取总PV
     */
    Integer selectAllPV(LocalDateTime createTime);

    /*
     * 获取总UV
     */
    Integer selectAllUV(LocalDateTime createTime);

    /*
     * 查询最大的id
     */
    Integer selectMaxId();

    /*
     * 更新
     */
    Boolean updateLogViewById(LogView logView);

    /**
     * 分页查询
     * @param start
     * @param end
     * @return
     */
    List<LogView> selectLogViewsByPage(@Param("start") int start, @Param("end") int end);

}
