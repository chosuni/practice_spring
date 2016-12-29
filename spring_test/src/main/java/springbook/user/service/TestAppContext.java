package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

import springbook.dao.UserDao;
import springbook.user.service.UserServiceImpl.TestUserService;


@Configuration
@Profile("test")  //Test용 Context 파일임으로 나타내는 Profile 설정(Context Grouping)
public class TestAppContext {

	
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
