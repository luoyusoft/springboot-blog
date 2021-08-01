package com.jinhx.blog.adaptor.operation;

import lombok.Data;

/**
 * CategoryAdaptorBuilder
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Data
public class CategoryAdaptorBuilder<T> {

    private Boolean parentName;
    private T data;

    /**
     * 构造方法私有化
     *
     * @param builder builder
     */
    private CategoryAdaptorBuilder(Builder<T> builder) {
        this.parentName = builder.parentName;
        this.data = builder.data;
    }

    public static class Builder<T> {
        // Optional parameters - initialize with default values
        private Boolean parentName;
        private T data;

        /**
         * 提供调用入口
         */
        public CategoryAdaptorBuilder<T> build(T data) {
            this.data = data;
            return new CategoryAdaptorBuilder<>(this);
        }

        public Builder() {
        }

        public Builder<T> setParentName() {
            this.parentName = true;
            return this;
        }

        public Builder<T> setParentName(Boolean parentName) {
            this.parentName = parentName;
            return this;
        }

        public Builder<T> setAll() {
            this.parentName = true;
            return this;
        }
    }

}
