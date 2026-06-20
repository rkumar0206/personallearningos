package com.rksdev.personallearningos.config;

import com.rksdev.security.web.LibraryUserIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // Instantiate and register the resolver directly from the security library
        resolvers.add(new LibraryUserIdArgumentResolver());
    }
}