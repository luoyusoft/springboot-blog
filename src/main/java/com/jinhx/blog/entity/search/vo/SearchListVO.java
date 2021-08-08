package com.jinhx.blog.entity.search.vo;

import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * SearchListVO
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Data
public class SearchListVO implements Serializable {

    private static final long serialVersionUID = 7627448363473287783L;

    private List<VideoVO> videoList;

    private List<ArticleVO> articleList;

}
