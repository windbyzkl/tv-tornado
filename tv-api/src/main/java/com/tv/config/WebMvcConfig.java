package com.tv.config;

import com.tv.interceptor.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
		.addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations("classpath:/static/")
				.addResourceLocations("file:e:/workspace/tv-dev/tv_dev_image/")
				.addResourceLocations("file:e:/workspace/resource/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry){
		registry.addInterceptor(new RequestInterceptor())
//				.addPathPatterns("/bgm/**")
//				.addPathPatterns("/video/upload","/video/userUnLike","/video/userLike")
				.addPathPatterns("/queryUser");
	}
	@Bean(initMethod="init")
	public ZKCuratorClient zkCuratorClient() {
		return new ZKCuratorClient();
	}

}
