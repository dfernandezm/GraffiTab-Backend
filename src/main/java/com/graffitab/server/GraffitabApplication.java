package com.graffitab.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude={SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, SpringBootWebSecurityConfiguration.class})
public class GraffitabApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GraffitabApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application ) {
        return application.sources(GraffitabApplication.class);
    }

    @Override
    public void onStartup( ServletContext servletContext ) throws ServletException {
        super.onStartup(servletContext);
        servletContext.addListener(requestContextListener());
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}

