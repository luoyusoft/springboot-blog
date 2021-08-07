package com.jinhx.blog.engine.article;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ArticleNodeChain
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class ArticleNodeChain{

    private LinkedHashMap<String, List<Class<? extends ArticleNode>>> nodesMaps = Maps.newLinkedHashMap();

    public <T extends ArticleNode> void add(Class<T> node) {
        add(node.getSimpleName(), node);
    }

    public <T extends ArticleNode> void add(String groupName, Class<T> node) {
        if (nodesMaps.containsKey(groupName)) {
            nodesMaps.get(groupName).add(node);
        } else {
            nodesMaps.put(groupName, Lists.newArrayList(node));
        }
    }

    Map<String, List<Class<? extends ArticleNode>>> getNodesMap() {
        return Collections.unmodifiableMap(nodesMaps);
    }
}
