package com.graffitab.server.config.web;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.graffitab.server.config.spring.CustomMappingJackson2HttpMessageConverter;
import com.mchange.v2.c3p0.ComboPooledDataSource;

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
@ImportResource("classpath:spring/spring-context.xml")
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private ComboPooledDataSource targetDataSource;
	
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
	
//	@Bean
//	public JsonDtoArgumentResolver jsonDtoArgumentResolver() {
//		return new JsonDtoArgumentResolver();
//	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
		converters.add(jacksonHttpMessageConverter());
	}
	
//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//		argumentResolvers.add(jsonDtoArgumentResolver());
//	}
	
	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}
	
	
	@Bean
	public IsolationLevelDataSourceAdapter dataSource() {
		IsolationLevelDataSourceAdapter dataSource = new IsolationLevelDataSourceAdapter();
		dataSource.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		dataSource.setTargetDataSource(targetDataSource);
		return dataSource;
	}
	
	@Bean
    public LocalSessionFactoryBean sessionFactory() {
		
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMappingDirectoryLocations(new ClassPathResource("hibernate-mappings"));
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
	
	Properties hibernateProperties() {
        return new Properties() {
			private static final long serialVersionUID = 1L;

			{
                setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
                setProperty("hibernate.show_sql", "false");
                setProperty("hibernate.hbm2ddl.auto", "validate");
            }
        };
    }


	
}
