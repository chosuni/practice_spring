package springbook.learningtest.spring.factorybean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
//설정파일을 지정하지 않으면 클래스 이름-context.xml 파일을 자동으로 읽는다.
public class FactoryBeanTest {
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactoryBean(){
		/*
		 * Factory Bean 자체를 가져올 경우
		 * Object message = context.getBean("&message");
		 */
		Object message = context.getBean("message");
		
		assertThat(message, is(Message.class));		
		assertThat( ((Message)message).getText(), is("Factory Bean"));
		
	}
}
