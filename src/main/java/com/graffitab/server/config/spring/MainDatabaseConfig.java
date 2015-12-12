package com.graffitab.server.config.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:jdbc.xml", "classpath:configurable-context.xml"})
public class MainDatabaseConfig extends DatabaseConfig {
	

}
