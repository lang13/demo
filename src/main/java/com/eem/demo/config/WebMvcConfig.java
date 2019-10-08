package com.eem.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * SpringMVC的配置文件
 * @author Administrator
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        //测试网页
        registry.addViewController("/test").setViewName("/test");
    }

    /**
     * 拦截器配置方法
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorConfig())
                //拦截全部
                .addPathPatterns("/**")
                //放行的请求
                .excludePathPatterns("/login","/register","/photo/downloadPhoto","test");
    }

    @Bean
    public InterceptorConfig interceptorConfig(){
        return new InterceptorConfig();
    }
}
