package com.graffitab.server.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.graffitab.server.config.web.WebConfig;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan(
		  basePackages={"com.graffitab.server"}, useDefaultFilters = false,
				  excludeFilters = {
			      @Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
		  }

		)
// Need to import the Security Configuration here, in the main context, otherwise the filterChain in web.xml fails
@Import(value={WebConfig.class})
@Order(1)
public class MainConfig { }
