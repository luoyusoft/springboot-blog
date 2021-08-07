package com.jinhx.blog.entity.operation;

import lombok.Data;

/**
 * TopAdaptorBuilder
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Data
public class TopAdaptorBuilder<T> {

    private Boolean description;
    private Boolean readNum;
    private Boolean watchNum;
    private Boolean likeNum;
    private Boolean cover;
    private Boolean tagList;
    private Boolean title;
    private T data;

    public TopAdaptorBuilder<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 构造方法私有化
     *
     * @param builder builder
     */
    private TopAdaptorBuilder(Builder<T> builder) {
        this.description = builder.description;
        this.readNum = builder.readNum;
        this.watchNum = builder.watchNum;
        this.likeNum = builder.likeNum;
        this.cover = builder.cover;
        this.tagList = builder.tagList;
        this.title = builder.title;
        this.data = builder.data;
    }

    public static class Builder<T> {
        // Optional parameters - initialize with default values
        private Boolean description = false;
        private Boolean readNum = false;
        private Boolean watchNum = false;
        private Boolean likeNum = false;
        private Boolean cover = false;
        private Boolean tagList = false;
        private Boolean title = false;
        private T data;

        /**
         * 提供调用入口
         */
        public TopAdaptorBuilder<T> build(T data) {
            this.data = data;
            return new TopAdaptorBuilder<>(this);
        }

        public TopAdaptorBuilder<T> build() {
            return new TopAdaptorBuilder<>(this);
        }

        public Builder() {
        }

        public Builder<T> setDescription() {
            this.description = true;
            return this;
        }

        public Builder<T> setDescription(Boolean description) {
            this.description = description;
            return this;
        }

        public Builder<T> setReadNum() {
            this.readNum = true;
            return this;
        }

        public Builder<T> setReadNum(Boolean readNum) {
            this.readNum = readNum;
            return this;
        }

        public Builder<T> setWatchNum() {
            this.watchNum = true;
            return this;
        }

        public Builder<T> setWatchNum(Boolean watchNum) {
            this.watchNum = watchNum;
            return this;
        }

        public Builder<T> setLikeNum() {
            this.likeNum = true;
            return this;
        }

        public Builder<T> setLikeNum(Boolean likeNum) {
            this.likeNum = likeNum;
            return this;
        }

        public Builder<T> setCover() {
            this.cover = true;
            return this;
        }

        public Builder<T> setCover(Boolean cover) {
            this.cover = cover;
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

        public Builder<T> setTitle() {
            this.title = true;
            return this;
        }

        public Builder<T> setTitle(Boolean title) {
            this.title = title;
            return this;
        }

        public Builder<T> setAll() {
            this.description = true;
            this.readNum = true;
            this.watchNum = true;
            this.likeNum = true;
            this.cover = true;
            this.tagList = true;
            this.title = true;
            return this;
        }
    }

}
