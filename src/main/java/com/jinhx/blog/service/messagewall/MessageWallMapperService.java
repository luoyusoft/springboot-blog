package com.jinhx.blog.service.messagewall;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.messagewall.vo.HomeMessageWallInfoVO;
import com.jinhx.blog.mapper.messagewall.MessageWallMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * MessageWallMapperService
 *
 * @author jinhx
 * @since 2021-04-11
 */
@Service
public class MessageWallMapperService extends ServiceImpl<MessageWallMapper, MessageWall> {

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    public HomeMessageWallInfoVO selectHomeMessageWallInfoVO() {
        // 当天零点
        LocalDateTime createTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        HomeMessageWallInfoVO homeMessageWallInfoVO = new HomeMessageWallInfoVO();
        homeMessageWallInfoVO.setMaxFloorNum(baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .select(MessageWall::getFloorNum)
                .orderByDesc(MessageWall::getFloorNum)
                .last("limit 1")).get(0).getFloorNum());
        homeMessageWallInfoVO.setAllCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        homeMessageWallInfoVO.setTodayCount(baseMapper.selectCount(new LambdaQueryWrapper<MessageWall>()
                .ge(MessageWall::getCreateTime, createTime)));
        return homeMessageWallInfoVO;
    }

    /**
     * 分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @param name 昵称
     * @param floorNum 楼层数
     * @return 留言列表
     */
    public PageData<MessageWall> selectMessageWallPage(Integer page, Integer limit, String name, Integer floorNum) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<MessageWall>(page, limit).getPage(), new LambdaQueryWrapper<MessageWall>()
                .like(StringUtils.isNotEmpty(name), MessageWall::getName, name)
                .eq(Objects.nonNull(floorNum), MessageWall::getFloorNum, floorNum)
                .orderByDesc(MessageWall::getMessageWallId)));
    }

    /**
     * 新增留言
     *
     * @param messageWall 留言
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMessageWall(MessageWall messageWall) {
        insertMessageWalls(Lists.newArrayList(messageWall));
    }

    /**
     * 批量新增留言
     *
     * @param messageWalls 留言列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMessageWalls(List<MessageWall> messageWalls) {
        if (CollectionUtils.isNotEmpty(messageWalls)){
            if (messageWalls.stream().mapToInt(item -> baseMapper.insert(item)).sum() != messageWalls.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 查询最大楼层
     *
     * @return 最大楼层
     */
    public Integer selectMaxFloorNum() {
        MessageWall messageWall = baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .select(MessageWall::getFloorNum)
                .orderByDesc(MessageWall::getFloorNum)
                .last("limit 1")).get(0);

        if (Objects.isNull(messageWall)){
            return null;
        }

        return messageWall.getFloorNum();
    }

    /**
     * 批量根据messageWallId删除留言
     *
     * @param messageWallIds messageWallIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessageWallsById(List<Long> messageWallIds) {
        if (CollectionUtils.isNotEmpty(messageWallIds)){
            if (baseMapper.deleteBatchIds(messageWallIds) != messageWallIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /********************** portal ********************************/

    /**
     * 查询总留言数
     *
     * @return 总留言数
     */
    public Integer selectTotalCount() {
        return baseMapper.selectCount(new LambdaQueryWrapper<>());
    }

    /**
     * 根据楼层范围查询留言列表
     *
     * @param minFloorNum minFloorNum
     * @param maxFloorNum maxFloorNum
     * @return  留言列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<MessageWall> selectMessageWallsByFloorNumRange(Integer minFloorNum, Integer maxFloorNum) {
        return baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .ge(MessageWall::getFloorNum, minFloorNum)
                .le(MessageWall::getFloorNum, maxFloorNum)
                .orderByDesc(MessageWall::getFloorNum)
                .orderByAsc(MessageWall::getMessageWallId));
    }

    /**
     * 根据messageWallId查询留言列表
     *
     * @param messageWallIds messageWallIds
     * @return 留言列表
     */
    public List<MessageWall> selecttMessageWallsById(List<Long> messageWallIds) {
        if (CollectionUtils.isEmpty(messageWallIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<MessageWall>().in(MessageWall::getMessageWallId, messageWallIds));
    }

}
