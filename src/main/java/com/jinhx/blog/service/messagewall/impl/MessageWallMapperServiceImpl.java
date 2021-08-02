package com.jinhx.blog.service.messagewall.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.messagewall.vo.HomeMessageWallInfoVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallListVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallVO;
import com.jinhx.blog.mapper.messagewall.MessageWallMapper;
import com.jinhx.blog.service.messagewall.MessageWallMapperService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MessageWallServiceImpl
 *
 * @author jinhx
 * @since 2021-04-11
 */
@Service
public class MessageWallMapperServiceImpl extends ServiceImpl<MessageWallMapper, MessageWall> implements MessageWallMapperService {

    @Value("${message.wall.default.profile}")
    private String messageWallDefaultProfile;

    @Value("${message.wall.default.manage.profile}")
    private String messageWallDefaultManageProfile;

    @Value("${message.wall.default.manage.name}")
    private String messageWallDefaultManageName;

    @Value("${message.wall.default.manage.email}")
    private String messageWallDefaultManageEmail;

    @Value("${message.wall.default.manage.website}")
    private String messageWallDefaultManageWebsite;

    /**
     * 后台获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeMessageWallInfoVO manageGetHomeMessageWallInfoVO() {
        // 当天零点
        LocalDateTime createTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        HomeMessageWallInfoVO homeMessageWallInfoVO = new HomeMessageWallInfoVO();
        homeMessageWallInfoVO.setMaxFloorNum(baseMapper.selectOne(new LambdaQueryWrapper<MessageWall>()
                .orderByDesc(MessageWall::getFloorNum)).getFloorNum());
        homeMessageWallInfoVO.setAllCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        homeMessageWallInfoVO.setTodayCount(baseMapper.selectCount(new LambdaQueryWrapper<MessageWall>()
                .ge(MessageWall::getCreateTime, createTime)));
        return homeMessageWallInfoVO;
    }

    /**
     * 后台新增留言
     *
     * @param messageWall 留言
     */
    @Override
    public void manageAddMessageWall(MessageWall messageWall) {
        // 新楼层
        if (MessageWall.REPLY_ID_LAYER_MASTER.equals(messageWall.getReplyId()) || messageWall.getReplyId() == null){
            messageWall.setReplyId(MessageWall.REPLY_ID_LAYER_MASTER);
            messageWall.setFloorNum(baseMapper.selectOne(new LambdaQueryWrapper<MessageWall>()
                    .orderByDesc(MessageWall::getFloorNum)).getFloorNum() + 1);
        } else {
            if (messageWall.getFloorNum() == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "floorNum不能为空");
            }
        }
        messageWall.setProfile(messageWallDefaultManageProfile);
        messageWall.setName(messageWallDefaultManageName);
        messageWall.setEmail(messageWallDefaultManageEmail);
        messageWall.setWebsite(messageWallDefaultManageWebsite);

        baseMapper.insert(messageWall);
    }

    /**
     * 后台分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @param name 昵称
     * @param floorNum 楼层数
     * @return 留言列表
     */
    @Override
    public PageUtils manageGetMessageWalls(Integer page, Integer limit, String name, Integer floorNum) {
        IPage<MessageWall> messageWallIPage = baseMapper.selectPage(new Query<MessageWall>(page, limit).getPage(), new LambdaQueryWrapper<MessageWall>()
                .like(ObjectUtil.isNotEmpty(name), MessageWall::getName, name)
                .eq(floorNum != null, MessageWall::getFloorNum, floorNum)
                .orderByDesc(MessageWall::getId));

        if (CollectionUtils.isEmpty(messageWallIPage.getRecords())){
            return new PageUtils(messageWallIPage);
        }

        List<MessageWall> messageWalls = baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .in(MessageWall::getId, messageWallIPage.getRecords().stream().map(MessageWall::getReplyId).distinct().collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(messageWalls)){
            messageWallIPage.setRecords(Lists.newArrayList());
            return new PageUtils(messageWallIPage);
        }

        // key：id，value：name
        Map<Integer, String> map = messageWalls.stream().collect(Collectors.toMap(MessageWall::getId, MessageWall::getName));

        List<MessageWallVO> messageWallVOs = Lists.newArrayList();
        messageWallIPage.getRecords().forEach(item -> {
            MessageWallVO messageWallVO = new MessageWallVO();
            BeanUtils.copyProperties(item, messageWallVO);
            messageWallVO.setReplyName(map.get(item.getReplyId()));
            messageWallVOs.add(messageWallVO);
        });

        IPage<MessageWallVO> messageWallVOIPage = new Page<>();
        BeanUtils.copyProperties(messageWallIPage, messageWallVOIPage);
        messageWallVOIPage.setRecords(messageWallVOs);

        return new PageUtils(messageWallVOIPage);
    }

    /**
     * 后台批量删除
     *
     * @param ids ids
     */
    @Override
    public void manageDeleteMessageWalls(Integer[] ids) {
        baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /********************** portal ********************************/

    /**
     * 新增留言
     *
     * @param messageWall 留言对象
     */
    @Override
    public void insertMessageWall(MessageWall messageWall) {
        messageWall.setName(MessageWall.NAME_PREFIX + messageWall.getName());
        // 新楼层
        if (MessageWall.REPLY_ID_LAYER_MASTER.equals(messageWall.getReplyId()) || messageWall.getReplyId() == null){
            messageWall.setReplyId(MessageWall.REPLY_ID_LAYER_MASTER);
            messageWall.setFloorNum(baseMapper.selectOne(new LambdaQueryWrapper<MessageWall>()
                    .orderByDesc(MessageWall::getFloorNum)).getFloorNum() + 1);
        }else {
            if (messageWall.getFloorNum() == null){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "floorNum不能为空");
            }
        }
        messageWall.setProfile(messageWallDefaultProfile);
        messageWall.setCreaterId(MessageWall.CREATER_UPDATER_GUEST_ID);
        messageWall.setUpdaterId(MessageWall.CREATER_UPDATER_GUEST_ID);

        baseMapper.insert(messageWall);
    }

    /**
     * 按楼层分页获取留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @return 留言列表
     */
    @Override
    public MessageWallListVO listMessageWalls(Integer page, Integer limit) {
        MessageWallListVO messageWallListVO = new MessageWallListVO();
        messageWallListVO.setTotalCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        if (messageWallListVO.getTotalCount() == null || messageWallListVO.getTotalCount() < 1){
            messageWallListVO.setHaveMoreFloor(false);
            messageWallListVO.setMessageWallVOList(Lists.newArrayList());
            return messageWallListVO;
        }

        Integer maxFloorNum = baseMapper.selectOne(new LambdaQueryWrapper<MessageWall>()
                .orderByDesc(MessageWall::getFloorNum)).getFloorNum() - (page - 1) * limit;
        Integer minFloorNum = maxFloorNum - limit + 1;

        messageWallListVO.setHaveMoreFloor(minFloorNum > 1);

        List<MessageWall> messageWalls = baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .ge(MessageWall::getFloorNum, minFloorNum)
                .le(MessageWall::getFloorNum, maxFloorNum)
                .orderByDesc(MessageWall::getFloorNum)
                .orderByAsc(MessageWall::getId));

        if (CollectionUtils.isEmpty(messageWalls)){
            messageWallListVO.setHaveMoreFloor(false);
            messageWallListVO.setMessageWallVOList(Lists.newArrayList());
            return messageWallListVO;
        }

        List<MessageWall> messageWallNames = baseMapper.selectList(new LambdaQueryWrapper<MessageWall>()
                .in(MessageWall::getId, messageWalls.stream().map(MessageWall::getReplyId).distinct().collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(messageWallNames)){
            messageWallListVO.setHaveMoreFloor(false);
            messageWallListVO.setMessageWallVOList(Lists.newArrayList());
            return messageWallListVO;
        }

        // key：id，value：name
        Map<Integer, String> map = messageWallNames.stream().collect(Collectors.toMap(MessageWall::getId, MessageWall::getName));

        List<MessageWallVO> messageWallVOs = Lists.newArrayList();
        messageWalls.forEach(item -> {
            MessageWallVO messageWallVO = new MessageWallVO();
            BeanUtils.copyProperties(item, messageWallVO);
            messageWallVO.setReplyName(map.get(item.getReplyId()));
            messageWallVOs.add(messageWallVO);
        });

        messageWallListVO.setMessageWallVOList(messageWallVOs);
        return messageWallListVO;
    }

}
