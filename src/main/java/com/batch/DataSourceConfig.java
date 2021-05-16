package com.batch;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
	
	@Autowired
	Environment env ;
	
	@Bean
	@Primary
	public DataSource mysql1DataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.db1.datasource.driverClassName"));
		dataSource.setUrl(env.getProperty("spring.db1.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.db1.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.db1.datasource.password"));
		
		return dataSource;
	}
}
