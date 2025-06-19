package com.xbt.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有域名
                .allowedMethods("*")        // 允许所有方法：GET, POST, PUT, DELETE 等
                .allowedHeaders("*")        // 允许所有请求头
                .allowCredentials(true);    // 是否允许发送Cookie

//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:80","http://localhost:5173") // Vue开发服务器的地址
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
    }
}