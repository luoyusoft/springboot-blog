package com.jinhx.blog.entity.builder;

import lombok.Data;

/**
 * VideoAdaptorBuilder
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Data
public class VideoAdaptorBuilder<T> {

    private Boolean categoryListStr;
    private Boolean tagList;
    private Boolean recommend;
    private Boolean top;
    private Boolean author;
    private T data;

    public VideoAdaptorBuilder<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 构造方法私有化
     *
     * @param builder builder
     */
    private VideoAdaptorBuilder(Builder<T> builder) {
        this.categoryListStr = builder.categoryListStr;
        this.tagList = builder.tagList;
        this.recommend = builder.recommend;
        this.top = builder.top;
        this.author = builder.author;
        this.data = builder.data;
    }

    public static class Builder<T> {
        // Optional parameters - initialize with default values
        private Boolean categoryListStr = false;
        private Boolean tagList = false;
        private Boolean recommend = false;
        private Boolean top = false;
        private Boolean author = false;
        private T data;

        /**
         * 提供调用入口
         */
        public VideoAdaptorBuilder<T> build(T data) {
            this.data = data;
            return new VideoAdaptorBuilder<>(this);
        }

        public VideoAdaptorBuilder<T> build() {
            return new VideoAdaptorBuilder<>(this);
        }

        public Builder() {
        }

        public Builder<T> setCategoryListStr() {
            this.categoryListStr = true;
            return this;
        }

        public Builder<T> setCategoryListStr(Boolean categoryListStr) {
            this.categoryListStr = categoryListStr;
            return this;
        }

        public Builder<T> setTagList() {
            this.tagList = true;
            return this;
        }

        public Builder<T> setTagList(Boolean tagList) {
            this.tagList = tagList;
            return this;
        }

        public Builder<T> setRecommend() {
            this.recommend = true;
            return this;
        }

        public Builder<T> setRecommend(Boolean recommend) {
            this.recommend = recommend;
            return this;
        }

        public Builder<T> setTop() {
            this.top = true;
            return this;
        }

        public Builder<T> setTop(Boolean top) {
            this.top = top;
            return this;
        }

        public Builder<T> setAuthor() {
            this.author = true;
            return this;
        }

        public Builder<T> setAuthor(Boolean author) {
            this.author = author;
            return this;
        }

        public Builder<T> setAll() {
            this.categoryListStr = true;
            this.tagList = true;
            this.recommend = true;
            this.top = true;
            this.author = true;
            return this;
        }
    }

}
