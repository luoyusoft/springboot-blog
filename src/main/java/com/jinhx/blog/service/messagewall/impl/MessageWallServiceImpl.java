package com.jinhx.blog.service.messagewall.impl;

import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.messagewall.vo.HomeMessageWallInfoVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallListVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallVO;
import com.jinhx.blog.service.messagewall.MessageWallMapperService;
import com.jinhx.blog.service.messagewall.MessageWallService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MessageWallServiceImpl
 *
 * @author jinhx
 * @since 2021-04-11
 */
@Service
public class MessageWallServiceImpl implements MessageWallService {

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

    @Autowired
    private MessageWallMapperService messageWallMapperService;

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeMessageWallInfoVO selectHomeMessageWallInfoVO() {
        return messageWallMapperService.selectHomeMessageWallInfoVO();
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
    @Override
    public PageData<MessageWall> selectMessageWallPage(Integer page, Integer limit, String name, Integer floorNum) {
        return messageWallMapperService.selectMessageWallPage(page, limit, name, floorNum);
    }

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     */
    @Override
    public void insertMessageWall(MessageWall messageWall) {
        // 新楼层
        if (MessageWall.REPLY_ID_LAYER_MASTER.equals(messageWall.getReplyId()) || Objects.isNull(messageWall.getReplyId())){
            messageWall.setReplyId(MessageWall.REPLY_ID_LAYER_MASTER);
            messageWall.setFloorNum(messageWallMapperService.selectMaxFloorNum() + 1);
        } else {
            if (Objects.isNull(messageWall.getFloorNum())) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "floorNum不能为空");
            }
        }
        messageWall.setProfile(messageWallDefaultManageProfile);
        messageWall.setName(messageWallDefaultManageName);
        messageWall.setEmail(messageWallDefaultManageEmail);
        messageWall.setWebsite(messageWallDefaultManageWebsite);

        messageWallMapperService.insertMessageWall(messageWall);
    }

    /**
     * 批量根据messageWallId删除留言
     *
     * @param messageWallIds messageWallIds
     */
    @Override
    public void deleteMessageWallsById(List<Long> messageWallIds) {
        messageWallMapperService.deleteMessageWallsById(messageWallIds);
    }

    /********************** portal ********************************/

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     */
    @Override
    public void insertPortalMessageWall(MessageWall messageWall) {
        messageWall.setName(MessageWall.NAME_PREFIX + messageWall.getName());
        // 新楼层
        if (MessageWall.REPLY_ID_LAYER_MASTER.equals(messageWall.getReplyId()) || Objects.isNull(messageWall.getReplyId())){
            messageWall.setReplyId(MessageWall.REPLY_ID_LAYER_MASTER);
            messageWall.setFloorNum(messageWallMapperService.selectMaxFloorNum() + 1);
        }else {
            if (Objects.isNull(messageWall.getFloorNum())){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "floorNum不能为空");
            }
        }
        messageWall.setProfile(messageWallDefaultProfile);
        messageWall.setCreaterId(MessageWall.CREATER_UPDATER_GUEST_ID);
        messageWall.setUpdaterId(MessageWall.CREATER_UPDATER_GUEST_ID);

        messageWallMapperService.insertMessageWall(messageWall);
    }

    /**
     * 按楼层分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @return 留言列表
     */
    @Override
    public MessageWallListVO selectPortalMessageWallPage(Integer page, Integer limit) {
        MessageWallListVO messageWallListVO = new MessageWallListVO();
        messageWallListVO.setTotalCount(messageWallMapperService.selectTotalCount());
        if (Objects.isNull(messageWallListVO.getTotalCount()) || messageWallListVO.getTotalCount() < 1){
            messageWallListVO.setHaveMoreFloor(false);
            messageWallListVO.setMessageWallVOList(Lists.newArrayList());
            return messageWallListVO;
        }

        Integer maxFloorNum = messageWallMapperService.selectMaxFloorNum() - (page - 1) * limit;
        Integer minFloorNum = maxFloorNum - limit + 1;

        messageWallListVO.setHaveMoreFloor(minFloorNum > 1);

        List<MessageWall> messageWalls = messageWallMapperService.selectMessageWallsByFloorNumRange(minFloorNum, maxFloorNum);

        if (CollectionUtils.isEmpty(messageWalls)){
            messageWallListVO.setHaveMoreFloor(false);
            messageWallListVO.setMessageWallVOList(Lists.newArrayList());
            return messageWallListVO;
        }
        List<Long> replyIds = messageWalls.stream()
                .map(MessageWall::getReplyId)
                .distinct()
                .filter(item -> !Objects.equals(item, MessageWall.REPLY_ID_LAYER_MASTER))
                .collect(Collectors.toList());

        List<MessageWall> messageWallNames = messageWallMapperService.selecttMessageWallsById(replyIds);

        // key：id，value：name
        Map<Long, String> map = messageWallNames.stream().collect(Collectors.toMap(MessageWall::getMessageWallId, MessageWall::getName));
        // 设置站长名称
        map.put(-1L, messageWallDefaultManageName);

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
