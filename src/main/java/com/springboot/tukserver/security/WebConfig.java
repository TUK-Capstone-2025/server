package com.springboot.tukserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://localhost:3000", "https://2b00-1-237-205-122.ngrok-free.app" ) // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP method
                .allowCredentials(true) // 쿠키 인증 요청 허용
                .allowedHeaders("*") // 또는 .allowedHeaders("Authorization", "Content-Type")
                .maxAge(3000);// 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + new File("uploads/").getAbsolutePath() + "/");
    }

}
