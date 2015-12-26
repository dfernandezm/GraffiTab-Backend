package com.graffitab.server.config.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionDefinition;

@Configuration
@ImportResource({"classpath:jdbc-dbcp.xml", "classpath:configurable-context.xml"})
@Profile("main")
public class MainDatabaseConfig {
	
	@Autowired
	private BasicDataSource targetDataSource;
	
	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}
	
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
        
        List<String> mappingFiles = new ArrayList<>();
        mappingFiles.add("hibernate-mappings/User.hbm.xml");
        mappingFiles.add("hibernate-mappings/Avatar.hbm.xml");
        mappingFiles.add("hibernate-mappings/Cover.hbm.xml");
        
        String[] mappingArray = new String[mappingFiles.size()];
       
        sessionFactory.setMappingResources(mappingFiles.toArray(mappingArray)); 
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
	
	public Properties hibernateProperties() {
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
