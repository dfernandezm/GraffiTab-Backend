package com.graffitab.server.config.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.StringUtils;

@Configuration
@ImportResource({"classpath:jdbc-dbcp.xml", "classpath:configurable-context.xml"})
@Profile("main")
public class MainDatabaseConfig {

	private static Logger LOG = LogManager.getLogger();

	@Value("${db.jdbcUrl:}")
	private String jdbcUrl;

	@Value("${db.username:}")
	private String dbUsername;

	@Value("${db.password:}")
	private String dbPassword;

	// Example: try to create a environment variable called
	// PROPERTY_SEVEN and see this populated!
	@Value("${property.seven:}")
	private String seven;

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
		changeTargetDataSourceIfNecessary();
		dataSource.setTargetDataSource(targetDataSource);
		return dataSource;
	}

	private void changeTargetDataSourceIfNecessary() {
		if (!StringUtils.isEmpty(jdbcUrl)) {
			LOG.info("Overriding database configuration with application properties: jbcUrl -> " +
					  jdbcUrl +", user -> " + dbUsername);

			targetDataSource.setUrl(jdbcUrl);
			targetDataSource.setUsername(dbUsername);
			targetDataSource.setPassword(dbPassword);
		}
	}

	@Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());

        List<String> mappingFiles = new ArrayList<>();
        mappingFiles.add("hibernate-mappings/User.hbm.xml");
        mappingFiles.add("hibernate-mappings/Asset.hbm.xml");

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
