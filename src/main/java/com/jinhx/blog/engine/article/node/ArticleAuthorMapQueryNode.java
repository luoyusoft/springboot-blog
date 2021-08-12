package com.jinhx.blog.engine.article.node;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.sys.SysUserMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ArticleAuthorMapQueryNode
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Slf4j
@Component
public class ArticleAuthorMapQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getAuthor() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Integer, String> map = Maps.newHashMap();
        context.getArticles().forEach(item -> {
            map.put(item.getId(), sysUserMapperService.getNicknameByUserId(item.getCreaterId()));
        });
        context.setArticleAuthorMap(map);
    }

    @Override
    public String getProcessorName() {
        return "ArticleAuthorMapQueryNode";
    }

}
