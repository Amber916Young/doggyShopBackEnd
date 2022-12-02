package com.doggy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        registry.addInterceptor(loginInterceptor())
                .excludePathPatterns("/wx/**")				//添加不拦截的请求路径
                .excludePathPatterns("/error/**")				//添加不拦截的请求路径
                .excludePathPatterns("/static/**")				//添加不拦截的请求路径
                .excludePathPatterns("/web/**")				//添加不拦截的请求路径
                .excludePathPatterns("/comment/**")				//添加不拦截的请求路径
                .excludePathPatterns("/**")				//添加不拦截的请求路径
                .excludePathPatterns("/view/**");				//添加不拦截的请求路径
//                .addPathPatterns("/**");						//添加需要拦截的路径
    }

    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
}
