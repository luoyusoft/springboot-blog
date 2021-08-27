package com.jinhx.blog.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jinhx.blog.common.util.SnowFlakeUtils;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.bill.Bill;
import com.jinhx.blog.entity.bill.BillType;
import com.jinhx.blog.entity.file.File;
import com.jinhx.blog.entity.file.FileChunk;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.operation.*;
import com.jinhx.blog.entity.sys.*;
import com.jinhx.blog.entity.video.Video;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充类
 *
 * @author jinhx
 * @since 2019-11-07
 */
@Component
public class MybatisPlusAutoFillHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     * @param metaObject metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        // 解决游客新增留言，日志记录问题
        if (!((metaObject.getOriginalObject() instanceof MessageWall && ((MessageWall) metaObject.getOriginalObject()).getCreaterId() != null)
                || metaObject.getOriginalObject() instanceof LogView)) {
            this.setFieldValByName("createrId", SysAdminUtils.getSysUserId(), metaObject);
            this.setFieldValByName("updaterId", SysAdminUtils.getSysUserId(), metaObject);
        }

        if (metaObject.getOriginalObject() instanceof Article) {
            this.setFieldValByName("articleId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Bill){
            this.setFieldValByName("billId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof BillType){
            this.setFieldValByName("billTypeId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof File){
            this.setFieldValByName("fileId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof FileChunk){
            this.setFieldValByName("fileChunkId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof LogView){
            this.setFieldValByName("logViewId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof MessageWall){
            this.setFieldValByName("messageWallId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Category){
            this.setFieldValByName("categoryId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof FriendLink){
            this.setFieldValByName("friendLinkId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Recommend){
            this.setFieldValByName("recommendId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Tag){
            this.setFieldValByName("tagId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof TagLink){
            this.setFieldValByName("tagLinkId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Top){
            this.setFieldValByName("topId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysMenu){
            this.setFieldValByName("sysMenuId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysParam){
            this.setFieldValByName("sysParamId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysRole){
            this.setFieldValByName("sysRoleId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysRoleMenu){
            this.setFieldValByName("sysRoleMenuId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysUser){
            this.setFieldValByName("sysUserId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof SysUserRole){
            this.setFieldValByName("sysUserRoleId", SnowFlakeUtils.getId(), metaObject);
        }
        if (metaObject.getOriginalObject() instanceof Video){
            this.setFieldValByName("videoId", SnowFlakeUtils.getId(), metaObject);
        }

        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("updateTime", now, metaObject);
    }

    /**
     * 更新时填充
     * @param metaObject metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 解决修改日志记录问题
        if (!(metaObject.getOriginalObject() instanceof LogView)){
            this.setFieldValByName("updaterId", SysAdminUtils.getSysUserId(), metaObject);
        }
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

}
