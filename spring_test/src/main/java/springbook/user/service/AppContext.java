package springbook.user.service;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springbook.dao.UserDao;
import springbook.issuetracker.sqlservice.EmbeddedDbSqlRegistry;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.service.UserServiceImpl.TestUserService;
import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlMapConfig;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.UserSqlMapConfig;


@Configuration //빈 설정정보를 관리하는 Context, 또한 이 선언된 클래스가 스스로도 빈으로 사용된다. 이에 따라 구현된 인터페이스를 타 클래스에서 @Autowired를 통해 주입받을 수 있게 된다.
@EnableTransactionManagement  // 	<tx:annotation-driven/> 대체.
@ComponentScan(basePackages="springbook.user") //@Component 에노테이선이 선언된 클래스를 Scan할 기준 패키지를 지정한다. (여러 패키지 지정 가능)

/*
 *  컨텍스트 클래스의 설정 병합(프로파일 설정여부에 관계없이 모두 지정해 놓음, 실제 적용할 클래스에서 @ActiveProfiles를 사용하여 지정함.)
 *  Static Class로 선언된 Context는 자동으로 import로 생락하며, 아래의 경우 SqlServiceContext.class는 새로운 메타 에노테이션으로 대체(EnableSqlService)하여 이를 주석처리 하였음.
 */
//@Import({SqlServiceContext.class, AppContext.TestAppContext.class, AppContext.ProductionAppContext.class})
@EnableSqlService
@PropertySource("/database.properties")  //등록된 리소스는 컨테이너가 관리하는 Environment 타입의 환경 객체로 접근할 수 있다. (컨텍스트가 저장한 빈을 @Autowired로 주입받을 수 있다)
//@ImportResource("/test-applicationContext2.xml")   // Spring Context xml 파일을 읽어와 이 설정 클래스를 초기화 한다.
public class AppContext implements SqlMapConfig{

	
	/*
	 * PropertyResource의 값을 그대로 가져와 자동으로 타입변환되어 주입. (치완자라고도 하며, xml 정의에서도 사용 가능) <property name="driverClass" value="${db.driverClass}">
	 * 이 기능을 활용하려면, PropertySorucesPlaceHolderConfigurer를 빈으로 등록해 줘야 한다. 
	 */
	@Value("${db.driverClass}")  //
	Class<? extends Driver> driverClass;
	
	@Value("${db.url}")
	String url;
	
	@Value("${db.username}")
	String username;
	
	@Value("${db.password}")
	String password;
	
	@Autowired
	Environment env;   //프로퍼티(@PropertySource)값을 자동 주입받음.
	
	//UserDaoJdbc가 @Component로 선언되어 있어 Bean으로 등록되고 이것이 자동으로 주입됨.
	@Autowired
	UserDao userDao;
	
	//필드의 타입 기준으로 빈을 주입받음
	//@Autowired
	//SqlService sqlService;
	
	
	//필드 이름을 기준으로 빈을 주입받음. (@Resource 에노테이션이 안될까?)
	//@Resource(name="embeddedDatabase")
	//EmbeddedDatabase embeddedDatabase;
	
	/*
	 * 
	 * 	<bean id="dataSource" class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
		<property name="driverClass" value="com.microsoft.sqlserver.jdbc.SQLServerDriver" ></property>
		<property name="url" value="jdbc:sqlserver://localhost:1433;databaseName=springDB" ></property>
		<property name="username" value="spring_user" ></property>
		<property name="password" value="!welcome0" ></property>			
	</bean>
	 */
	@Bean  // 설정파일의 bean 태그를 대체... 메소드명은 id값을 사용함.	
	public DataSource dataSource(){
		
		//속성값을 그대로 지정해야 하므로 원래의 class 속성 객체를 생성함.
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(this.driverClass);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		/*
		try {
			dataSource.setDriverClass((Class<? extends Driver>)Class.forName(env.getProperty("db.driverClass")));			
		} catch(ClassNotFoundException cnfe){
			throw new RuntimeException(cnfe);
		}
		
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));
		*/
		
		return dataSource;
	}
	

	/*
	 * 	<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>		
	</bean>
	 * 
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(this.dataSource());
		
		return transactionManager;
	
	}
	
	/*
	 * 	<bean id="userDao" class="springbook.user.dao.UserDaoJdbc">
		<property name="dataSource" ref="dataSource"></property>		
		<property name="sqlService" ref="sqlService"></property>
	</bean>	


	@Bean  
	public UserDao userDao() {
		
		//실제로 객체를 생성하지 않고, 컨텍스트에 의해 생성된 UserDaoJdbc 객체를 반환한다.
		return new UserDaoJdbc();
	}
	 */	
	
	/*
	 * 	<bean id="userService" class="springbook.user.service.UserServiceImpl">
		<qualifier value="no1"/>	
		<property name="userDao" ref="userDao"></property>
		<property name="mailSender" ref="mailSender"></property>		
	</bean>
	
	Bean을 자동주입을 하려면, UserServiceImpl 관련 속성을 autowired로 설정해 두어야 한다.
	
	@Bean
	public UserService userService(){
		UserServiceImpl service = new UserServiceImpl();
		service.setUserDao(this.userDao);
		service.setMailSender(this.mailSender());
		return service;
	}
	 */	
	
	
	/*
	 * Property를 치완자를 사용하여 자동 주입방식을 사용하기 위한 Configurer 빈 등록(반드시 static를 사용하여야 함)
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer(){
		return new PropertySourcesPlaceholderConfigurer();
	}

	
	public Resource getSqlMapResource(){		
		return new ClassPathResource("sqlmap.xml", UserDao.class);
	}
	
	
	@Configuration   //Static로 설정된 @Configuration은 스프링이 자동으로 설정 파일로 포함시켜 준다(@Import로 선언하지 않아도 됨)
	@Profile("test")  //Test용 Context 파일임으로 나타내는 Profile 설정(Context Grouping)
	public static class TestAppContext {

		
		/*
		 * 	<bean id="testUserService" class="springbook.user.service.UserServiceImpl$TestUserService" parent="userService"></bean>
		 */
		@Bean
		public UserService testUserService(){
			//TestUserService 클래스도 UserServiceImpl을 상속받아 자동으로 bean 등록대상이므로 객체를 생성하여 반환하도록 하면 된다.
			
			return new TestUserService();
			/*
			TestUserService testService = new TestUserService();
			testService.setUserDao(this.userDao);
			testService.setMailSender(this.mailSender());
			
			return testService;
			*/
		}
		
		/*
		 * 	<bean id="mailSender" class="springbook.user.service.DummyMailSender"></bean>
		 */
		@Bean
		public MailSender mailSender(){
			
			return new DummyMailSender();
			
		}

		
	}
	

	@Configuration
	@Profile("production")   //프로파일이 지정되지 않으면  "default" 이름으로 등록되며 @ActiveProfiles에 지정하지 않더라도 자동 로딩된다.	                                  //@Profile이 설정된 context 클래스는 반드시 @ActiveProfiles를 사용하여 사용할 대상 프로파일을 활성 프로파일을 지정하지 않으면 @Import, @ContextConfiguration으로 명시하더라도 로딩되지 않는다.
	public static class ProductionAppContext {

		
		/*
		 * 실제 운영환경이므로 JavaMail을 사용하는 MailSender Bean 등록.
		 * 
		 * 같은 타입이면서 아이디도 같은 두개의 빈이 있으면, 스프링이 빈 정보를 읽는 순서에 따라 뒤의 빈 설정이 앞의 빈 설정에 우선하여 설정된다.
		 */
		@Bean
		public MailSender mailSender(){
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("mail.company.com");
			return mailSender;
		}
		
	}
	
	
	
}
