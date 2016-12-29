package springbook.learningtest.spring.ioc.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class ServiceConfig {

	

	@Bean
	public DataSource dataSource(){
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(org.mariadb.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mariadb://localhost:3306/springDB");
		dataSource.setUsername("spring_user");
		dataSource.setPassword("!welcome0");
		
		return dataSource();
		
	}
}
