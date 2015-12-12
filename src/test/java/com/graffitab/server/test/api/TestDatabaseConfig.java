package com.graffitab.server.test.api;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.graffitab.server.config.spring.DatabaseConfig;

@Configuration
@Profile("unit-test")
@ImportResource({"classpath:jdbc-test.xml"})
public class TestDatabaseConfig extends DatabaseConfig {

	@Bean
    public LocalSessionFactoryBean sessionFactory() {	
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMappingDirectoryLocations(new ClassPathResource("hibernate-mappings"));
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
	
	@Override
	public Properties hibernateProperties() {
        return new Properties() {
			private static final long serialVersionUID = 1L;

			{
                setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
                setProperty("hibernate.show_sql", "false");
                setProperty("hibernate.hbm2ddl.auto", "create");
            }
        };
    }	
}
