package com.batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;
  
  @Autowired
  public DataSourceConfig dataSource;

  @Bean
  public JdbcCursorItemReader<Person> reader() {
	  JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<Person>();
	  reader.setDataSource(dataSource.mysql1DataSource());
	  reader.setSql("SELECT firstName, lastName FROM people");
	  reader.setRowMapper(new UserRowMapper());
	  
	  return reader;
  }
  
  public class UserRowMapper implements RowMapper<Person>{

	  @Override
	  public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
	   Person user = new Person();
	   user.setFirstName(rs.getString("firstName"));
	   user.setLastName(rs.getString("lastName"));
	   
	   return user;
	  }
	  
	 }

  @Bean
  public PersonItemProcessor processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Person> writer() {
    return new JdbcBatchItemWriterBuilder<Person>()
      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
      .sql("INSERT INTO people (firstName, lastName) VALUES (:firstName, :lastName)")
      .dataSource(dataSource.mysql2DataSource())
      .build();
  }
  
  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("importUserJob")
      .incrementer(new RunIdIncrementer())
      .listener(listener)
      .flow(step1)
      .end()
      .build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<Person> writer) {
    return stepBuilderFactory.get("step1")
      .<Person, Person> chunk(10)
      .reader(reader())
      .processor(processor())
      .writer(writer)
      .build();
  }

}