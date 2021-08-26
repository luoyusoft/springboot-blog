package com.jinhx.blog.engine.article.node.select;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.sys.SysUserMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SelectArticleAuthorMapNode
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Component
public class SelectArticleAuthorMapNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getAuthor() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Long, String> map = Maps.newHashMap();
        context.getArticles().forEach(item -> {
            map.put(item.getArticleId(), sysUserMapperService.selectNicknameBySysUserId(item.getCreaterId()));
        });
        context.setArticleAuthorMap(map);
    }

    @Override
    public String getProcessorName() {
        return "SelectArticleAuthorMapNode";
    }

}
