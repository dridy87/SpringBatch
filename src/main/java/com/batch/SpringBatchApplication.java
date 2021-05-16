package com.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchApplication extends DefaultBatchConfigurer {

	@Override
    public void setDataSource(DataSource dataSource) {
        // 여기를 비워놓는다
    }
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}
