package com.graffitab.server.config.spring;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.StringUtils;

@Configuration
@ImportResource({"classpath:configurable-context.xml"})
public class CommonsDbcpConfig {
	
	private static Logger LOG = LogManager.getLogger();
	
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
		dataSource.setTargetDataSource(targetDataSource());
		return dataSource;
	}
	
	public BasicDataSource targetDataSource() {
		
		String envDbUrl = System.getenv("DATABASE_URL");
		String username = "";
		String password = "";
		String jdbcUrl = "";
	
		try {
			
			if (!StringUtils.isEmpty(envDbUrl)) {
				LOG.info("Reading datasource config from env variable");
				URI dbUri = new URI(envDbUrl);
			    username = dbUri.getUserInfo().split(":")[0];
			    password = dbUri.getUserInfo().split(":")[1];
			    jdbcUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();

			} else {
				jdbcUrl = "jdbc:mysql://localhost:3307/graffitab";
				username = "root";
				password = "";
			}
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		LOG.info("*** Configuring dataSource with jdbcUrl: " + jdbcUrl + ", username: " + username + " ***");
		
		BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(jdbcUrl + "?useUnicode=true&amp;characterEncoding=UTF-8");
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setInitialSize(5);
        basicDataSource.setMaxIdle(5);
        basicDataSource.setMinIdle(2);
		
		return basicDataSource;
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
