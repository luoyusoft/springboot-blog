package com.jinhx.blog.entity.article;

import lombok.Builder;
import lombok.Data;

/**
 * ArticleBuilder
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Builder(toBuilder = true)
@Data
public class ArticleBuilder {

    @lombok.Builder.Default
    private Boolean categoryListStr = false;

    @lombok.Builder.Default
    private Boolean tagList = false;

    @lombok.Builder.Default
    private Boolean recommend = false;

    @lombok.Builder.Default
    private Boolean top = false;

    @lombok.Builder.Default
    private Boolean author = false;

}
