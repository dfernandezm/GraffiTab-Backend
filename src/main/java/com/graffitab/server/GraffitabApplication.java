package com.graffitab.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.graffitab.server.config.spring.MainConfig;
import com.graffitab.server.config.spring.MainDatabaseConfig;

@SpringBootApplication
@EnableAutoConfiguration(exclude={SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, SpringBootWebSecurityConfiguration.class})
public class GraffitabApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GraffitabApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application ) {
        return application.sources(GraffitabApplication.class, MainConfig.class, MainDatabaseConfig.class);
    }

    @Override public void onStartup( ServletContext servletContext ) throws ServletException {
        super.onStartup(servletContext);
        servletContext.addListener(requestContextListener());
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }


    //TODO: These three beans are redundant, still not sure why I needed to put them here, but until more functionality is done
    //and tested I'll keep the code here, but commented out the registration. Need to figure out where are they being registered and
    //why registering them here is not needed

    // =================================================================================================================================

    //@Bean
    public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                dispatcherServlet);
        registration.addUrlMappings("/");
        Map<String,String> params = new HashMap<String,String>();
        params.put("contextClass","org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
        params.put("contextConfigLocation","com.graffitab.server.config.web.WebConfig");
        registration.setInitParameters(params);
        registration.setLoadOnStartup(2);
        return registration;
    }

    //@Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return registrationBean;
    }



    // Spring security filter chain should be handled by the autoconfiguration (GraffitabSecurityConfig),
    // but it is disabled there and added manually here because of not being able to delete default
    // login form configuration
    // @Bean
    public FilterRegistrationBean securityFilterChainRegistration() {
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.setTargetBeanName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(delegatingFilterProxy);
        registrationBean.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}

