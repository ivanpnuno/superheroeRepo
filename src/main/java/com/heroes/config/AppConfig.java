package com.heroes.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.heroes.controller.HeroController;
import com.heroes.exceptions.CustomExceptionHandler;

import com.heroes.exceptions.ConstraintViolationExceptionHandler;

@Component
@ApplicationPath("/api")
@Configuration
@EnableCaching
public class AppConfig extends ResourceConfig {

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("heroes");
	}

	public AppConfig() {
		packages("com.heroes");
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		this.register(ConstraintViolationExceptionHandler.class);
		this.register(CustomExceptionHandler.class);
		this.register(HeroController.class);
	}
}
