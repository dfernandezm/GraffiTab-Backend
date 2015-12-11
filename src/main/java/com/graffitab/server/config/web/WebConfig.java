package com.graffitab.server.config.web;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.graffitab.server.config.spring.CustomMappingJackson2HttpMessageConverter;
import com.graffitab.server.config.spring.JsonDtoArgumentResolver;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(
  basePackages={"com.graffitab.server"}, useDefaultFilters = false, 
  includeFilters = { 
	      @Filter(type = FilterType.ANNOTATION, classes = {Controller.class}),
	      @Filter(type = FilterType.ANNOTATION, classes = {Component.class})
  }
)
@Order(2)
public class WebConfig extends WebMvcConfigurationSupport {
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.requestMappingHandlerAdapter();
		return requestMappingHandlerAdapter;
	}
	
	
	@Bean
	public CommonsMultipartResolver commonsMultipartResolver() {
		CommonsMultipartResolver commonsMultipart = new CommonsMultipartResolver();
		return commonsMultipart;
	}
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

	@Bean
	public MappingJackson2HttpMessageConverter delegateJacksonHttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter();
	}
	
	@Bean(name = "delegate")
	public MappingJackson2HttpMessageConverter delegate() {
		MappingJackson2HttpMessageConverter conv = new MappingJackson2HttpMessageConverter();
		return conv;
	}
	
	
	@Bean
	public CustomMappingJackson2HttpMessageConverter jacksonHttpMessageConverter() {
		return new CustomMappingJackson2HttpMessageConverter();
	}
	
	@Bean
	public JsonDtoArgumentResolver jsonDtoArgumentResolver() {
		JsonDtoArgumentResolver resolver = new JsonDtoArgumentResolver();
		return resolver;
	}
	
	@Bean
	public List<HttpMessageConverter<?>> converters() {
		return requestMappingHandlerAdapter().getMessageConverters();
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
		converters.add(jacksonHttpMessageConverter());
	}
	
	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(jsonDtoArgumentResolver());
	}
	
	
}
