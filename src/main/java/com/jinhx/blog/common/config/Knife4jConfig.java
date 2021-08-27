package com.jinhx.blog.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Knife4jConfig
 *
 * @author jinhx
 * @since 2019-10-07
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * @author jinhaoxun
     * @description 配置token，以及设置扫描包的路径
     * @return Docket
     */
    @Bean("createRestApi")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Blog Knife4j 接口文档")
                        .description("Blog Knife4j 接口文档")
                        .contact(new Contact("Jinhx", "https://jinhx.cc", "jinhx128@163.com"))
                        .version("1.0.0")
                        .termsOfServiceUrl("https://jinhx.cc/doc.html")
                        .build())
                .groupName("1.0.0 版本")
                .select()
                //此处添加需要扫描接口的包路径
                .apis(RequestHandlerSelectors.basePackage("com.jinhx.blog.controller"))
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                // 配置header参数
                .securitySchemes(security());
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey("token", "token", "header")
        );
    }

}
