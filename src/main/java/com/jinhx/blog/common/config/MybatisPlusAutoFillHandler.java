package com.jinhx.blog.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jinhx.blog.common.util.SnowFlakeUtils;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.messagewall.MessageWall;
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

        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("createTime", now, metaObject);
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
