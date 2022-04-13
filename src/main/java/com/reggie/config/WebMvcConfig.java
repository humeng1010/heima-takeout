package com.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * mvc静态资源映射 配置类
 */

@Slf4j
@Configuration//说明这个是SpringBoot的配置类
//让这个类继承WebMvcConfigurationSupport
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * 重写WebMvcConfigurationSupport中的addResourceHandlers方法
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //调用registry的addResourceHandler添加资源处理器，
        //再调用addResourceLocations添加资源路径（classpath对应的是resource目录）
            //后台
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
            //前台
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }
}
