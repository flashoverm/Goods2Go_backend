package com.goods2go.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "geoEntityManagerFactory",
  transactionManagerRef = "geoTransactionManager",
  basePackages = { "com.goods2go.geo.repositories" }
)
public class GeoDbConfig {

	@Bean(name = "geoDataSource")
	@ConfigurationProperties(prefix = "geo.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
  
  	@Bean(name = "geoEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean geoEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("geoDataSource") DataSource dataSource) {
  		
  		Map<String, Object> properties = new HashMap<String, Object>();
  		properties.put("jpa.show-sql", "true");  
		  //properties.put("jpa.properties.hibernate.dialect", "PostgreSQLDialect");  
		  //properties.put("jpa.generate-ddl", "true");
		properties.put("hibernate.hbm2ddl.auto", "update");
  		
		return builder
			.dataSource(dataSource)
			.packages("com.goods2go.geo.models")
			.persistenceUnit("geo").properties(properties)
			.build();
	}
    
	@Bean(name = "geoTransactionManager")
	public PlatformTransactionManager geoTransactionManager(
			@Qualifier("geoEntityManagerFactory") EntityManagerFactory geoEntityManagerFactory) {
		return new JpaTransactionManager(geoEntityManagerFactory);
  }
}