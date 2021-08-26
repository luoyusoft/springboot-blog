package com.jinhx.blog.engine.article;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.operation.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ArticleQueryContextInfo
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Data
@Slf4j
public class ArticleQueryContextInfo<T extends BaseRequestDTO> implements Serializable {

    private static final long serialVersionUID = -1958101545368445429L;

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 入参 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 基础入参
     */
    private T queryDTO;

    /**
     * 配置需要查询的参数
     */
    private ArticleBuilder articleBuilder;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 页数
     */
    private Integer limit;

    /**
     * 文章id列表
     */
    private List<Long> articleIds;

    /**
     * 发布状态
     */
    private Boolean publish;

    /**
     * 分类
     */
    private Long categoryId;

    /**
     * 时间排序
     */
    private Boolean latest;

    /**
     * 点赞量排序
     */
    private Boolean like;

    /**
     * 阅读量排序
     */
    private Boolean read;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 入参 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 中间数据 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 是否需要执行下一组节点
     */
    private Boolean exNextNode = true;

    /**
     * 文章部分信息
     */
    private IPage<Article> articleIPage;

    /**
     * k：文章id，value：文章所属分类，以逗号分
     */
    private Map<Long, String> articleCategoryListStrMap = Maps.newHashMap();

    /**
     * k：文章id，value：所属标签
     */
    private Map<Long, List<Tag>> articleTagListMap = Maps.newHashMap();

    /**
     * k：文章id，value：推荐
     */
    private Map<Long, Boolean> articleRecommendMap = Maps.newHashMap();

    /**
     * k：文章id，value：置顶
     */
    private Map<Long, Boolean> articleTopMap = Maps.newHashMap();

    /**
     * k：文章id，value：文章作者
     */
    private Map<Long, String> articleAuthorMap = Maps.newHashMap();

    /**
     * 文章部分信息
     */
    private List<Article> articles;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 中间数据 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 结果 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 文章完整信息
     */
    private IPage<ArticleVO> articleVOIPage;

    /**
     * 文章完整信息
     */
    private List<ArticleVO> articleVOs;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 结果 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 初始化数据，并进行数据校验，可以改为多线程！
     *
     * @param queryDTO queryDTO
     * @return ArticleQueryContextInfo
     */
    public static <T extends BaseRequestDTO> ArticleQueryContextInfo<T> create(T queryDTO) {
        if (ObjectUtils.isEmpty(queryDTO.getLogStr())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "logStr不能为空");
        }

        return new ArticleQueryContextInfo<>(queryDTO);
    }

    public ArticleQueryContextInfo() {
    }

    public ArticleQueryContextInfo(T queryDTO) {
        this.queryDTO = queryDTO;
    }

}
