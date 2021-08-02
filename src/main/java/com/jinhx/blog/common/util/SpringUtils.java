package com.jinhx.blog.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 加载和获取spring上下文环境，支持xml文件配置方式和静态代码加载方式两种形式
 *
 * @author jinhx
 * @since 2021-08-02
 */
@Component
@Slf4j
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public SpringUtils() {
        log.info("初始化SpringUtils");
    }

    /**
     * spring配置文件加载方式
     *
     * @param context context
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * 应用启动时静态加载方式
     *
     * @param context context
     */
    public static void load(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * 获取spring上下文 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext context() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未注入，请在Spring配置文件中定义SpringContext或在应用启动时手动加载");
        }
        return applicationContext;
    }

    /**
     * 根据名称获取bean
     *
     * @param name name
     * @return T
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz clazz
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据类型和名称获取bean
     *
     * @param name name
     * @param clazz clazz
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取属性
     *
     * @param key key
     * @return String
     */
    public static String getProperty(String key) {
        return applicationContext.getBean(Environment.class).getProperty(key);
    }

}
