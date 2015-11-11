package com.graffitab.server.config.web;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.graffitab.server.config.spring.CustomMappingJackson2HttpMessageConverter;
import com.graffitab.server.config.spring.JsonDtoArgumentResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(
  basePackages={"com.graffitab.server.api"}, useDefaultFilters = false, 
  includeFilters = { 
		      @Filter(type = FilterType.ANNOTATION, classes = {Controller.class}),
		      @Filter(type = FilterType.ANNOTATION, classes = {Component.class})
  }
)
public class WebConfig extends WebMvcConfigurerAdapter {

	@Bean
	public MappingJackson2HttpMessageConverter delegateJacksonHttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter();
	}
	
	@Bean
	public CustomMappingJackson2HttpMessageConverter jacksonHttpMessageConverter() {
		return new CustomMappingJackson2HttpMessageConverter();
	}
	
	@Bean
	public JsonDtoArgumentResolver jsonDtoArgumentResolver() {
		return new JsonDtoArgumentResolver();
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
		converters.add(jacksonHttpMessageConverter());
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(jsonDtoArgumentResolver());
	}
	
//	@Bean
//	public HibernateTransactionManager transactionManager() {
//		return null;
//	}
//	
//	@Bean
//	public LocalSessionFactoryBean sessionFactory() {
//		return null;
//	}
//	

	
}
