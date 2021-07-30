package com.jinhx.blog.mapper.messagewall;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinhx.blog.entity.messagewall.vo.MessageWallVO;
import com.jinhx.blog.entity.messagewall.MessageWall;
import io.lettuce.core.dynamic.annotation.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MessageWallMapper
 *
 * @author jinhx
 * @since 2021-04-15
 */
public interface MessageWallMapper extends BaseMapper<MessageWall> {

    /**
     * 分页获取留言列表
     * @param page 页码
     * @param params 参数
     * @return 留言列表
     */
    List<MessageWallVO> selectMessageWallVOs(Page<MessageWallVO> page, @Param("params") Map<String, Object> params);

    /**
     * 获取最大楼层数
     * @return 最大楼层数
     */
    Integer selectMaxFloorNum();

    /**
     * 获取总留言数
     * @return 总留言数
     */
    Integer selectMessageWallCount();

    /**
     * 获取今天留言数
     * @param createTime 今天零点时间
     * @return 今天留言数
     */
    Integer selectTodayCount(LocalDateTime createTime);

    /**
     * 是否有更多楼层
     * @param floorNum 最大楼层数
     * @return 是否有更多楼层
     */
    Boolean haveMoreFloor(@Param("floorNum") Integer floorNum);

    /**
     * 按楼层分页获取留言列表
     * @param minFloorNum 最小楼层
     * @param maxFloorNum 最大楼层
     * @return 留言列表
     */
    List<MessageWallVO> selectMessageWallVOListByFloor(@Param("minFloorNum") Integer minFloorNum, @Param("maxFloorNum") Integer maxFloorNum);

}
