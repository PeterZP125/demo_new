package com.peter.mall.config;

import com.peter.mall.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        String[] excludePatterns = new String[]{"/error", "/static/**", "/views/**", "/res/**", "/webjars/bootstrap/**", "/asserts/**"};
//        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/error");
//        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/","/css/**","/js/**","/img/**","/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //排除静态资源拦截
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

}
