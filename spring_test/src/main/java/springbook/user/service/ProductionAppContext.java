package springbook.user.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 운영환경에서는 반드시 필요하지만, 테스트 환경에서는 배제되어야 할 빈 Context 을 정의한 클래스
 * @author 상곤
 *
 */

@Configuration
@Profile("production")   //프로파일이 지정되지 않으면  "default" 이름으로 등록되며 @ActiveProfiles에 지정하지 않더라도 자동 로딩된다.
                                  //@Profile이 설정된 context 클래스는 반드시 @ActiveProfiles를 사용하여 사용할 대상 프로파일을 활성 프로파일을 지정하지 않으면 @Import, @ContextConfiguration으로 명시하더라도 로딩되지 않는다.
public class ProductionAppContext {

	
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
