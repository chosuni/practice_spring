package springbook.user.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import springbook.issuetracker.sqlservice.EmbeddedDbSqlRegistry;
import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlMapConfig;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;


@Configuration
public class SqlServiceContext {
	
	
	//AppContext 클래스가 SqlMapConfig를 구현하고 있고 자체가 빈이므로 해당 객체가 주입됨.
	@Autowired
	SqlMapConfig sqlMapConfig; 
	
	/*
	 * <bean id="sqlService" class="springbook.user.sqlservice.OxmSqlService">
		<property name="unmarshaller" ref="unmarshaller"></property>	
		<property name="sqlmap" value="classpath:springbook/dao/sqlmap.xml"></property>
		<property name="sqlRegistry" ref="sqlRegistry" ></property>
		
	</bean>
	 */
	@Bean
	public SqlService sqlService(){
		
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(this.unmarshaller());
		sqlService.setSqlRegistry(this.sqlRegistry());
		sqlService.setSqlmap(this.sqlMapConfig.getSqlMapResource());   //다른 SQL Map을 사용하려면 직접 sqlmap을 설정한다.
		
		
		return sqlService;
		
	}
	
	
	/*
	 * 	<bean id="sqlRegistry" class="springbook.issuetracker.sqlservice.EmbeddedDbSqlRegistry" >
		<property name="dataSource" ref="embeddedDatabase"></property> <!-- jdbc:embedded-database 태그로 생성된  EmbeddedDatabase 객체를 dataSource로 설정함.-->	
	</bean>
	 */
	
	@Bean
	public SqlRegistry sqlRegistry(){
		
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(this.embeddedDatabase());
		return sqlRegistry;
	}
	
	/*
	 * 	<bean id="unmarshaller" class="org.springframework.oxm.jaxb.Jaxb2Marshaller">
		<property name="contextPath" value="springbook.user.sqlservice.jaxb"></property>	
	</bean>        
	 */
	@Bean
	public Unmarshaller unmarshaller(){
		
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("springbook.user.sqlservice.jaxb");
		return marshaller;
	}
	
	
	@Bean
	public DataSource embeddedDatabase(){
		return new EmbeddedDatabaseBuilder()
			.setName("embeddedDatabase")
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql")
			.build();
	}
	
}
