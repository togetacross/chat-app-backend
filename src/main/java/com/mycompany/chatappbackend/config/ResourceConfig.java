package com.mycompany.chatappbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

	@Value("${files_storage.dir}")
	private String filesStorage;
	

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/files/**")
				.addResourceLocations("files:" + filesStorage);
	}
}
