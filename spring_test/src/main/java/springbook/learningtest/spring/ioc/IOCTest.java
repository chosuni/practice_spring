package springbook.learningtest.spring.ioc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.learningtest.spring.ioc.bean.AnnotatedHello;
import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.StringPrinter;
import springbook.learningtest.spring.ioc.config.AnnotatedHelloConfig;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

public class IOCTest {

	

	/**
	 * IoC 컨테이너에 빈을 등록하는 Test
	 */
	@Test
	public void test1(){
		
		//코드를 통해 빈 설정 메타정보를 입력하기 위한 기본 빈 컨테이너.
		StaticApplicationContext ac = new StaticApplicationContext();
		
		//Hello 클래스를  "hello1"라는 이름으로 싱글톤 빈으로 컨테이너에 등록한다. (내부적으로 BeanDefinition을 생성하여 등록함)
		ac.registerSingleton("hello1", Hello.class);
		
		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));
		
		
		//Spring의 XML 매타정보 중 <bean> 태그에 해당하는 메타정보를 정의하는 클래스
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class); 
		
		// 빈의 <property> 태그 정의
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		
		//BeanDefinition을 IoC 컨테이너에 등록한다.
		ac.registerBeanDefinition("hello2", helloDef);
		
		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));
		
		//최초 등록한 인스턴스와 다른 인스턴스의 빈이 얻어지는지 여부(각각의 인스턴스의 빈을 얻을 수 있음)
		assertThat(hello1, is(not(hello2)));
		assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
				
	}
	
	
	
	/**
	 * Bean Definition을 사용한 IoC 테스트
	 * Bean의 ref 속성을 이용한 빈 상호간의 IoC 주입
	 */
	@Test
	public void registerBeanWithDependency(){

		StaticApplicationContext ac = new StaticApplicationContext();
		
		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer") );   // 다른 참조의 빈을 속성으로 DI
		
		ac.registerBeanDefinition("hello", helloDef);
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
		

		
	}
	
	@Test
	public void genericApplicationContext(){
		
		
		//StaticApplicationContext 보다 XML 파일과 같이 외부의 리소스에 있는 빈 설정 메타정보를 리더를 통해 읽어 매타 정보로 전환/사용할 수 있는 부가적인 기능을 제공한다.
		//실제 개발에서 일반적으로 많이 쓰이는 빈 컨텍스트임. 
		GenericApplicationContext gac = new GenericApplicationContext();
		
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(gac);		
		//기본적으로 클래스패스로 정의된 리소스로부터 파일을 읽는다.
		reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
				
		//모든 매타정보 등록이 완료됐으니 애플리케이션 컨텍스트를 초기화 하라는 명령.
		gac.refresh();
		
		
		Hello hello = gac.getBean("hello", Hello.class);
		hello.print();
		assertThat(gac.getBean("printer").toString(), is("Hello Spring"));
		
		
		//GenericApplicationContext와 XmlBeanDefinitionReader을 이용하여 초기화 하는것을 한번해 하려면 GenericXmlApplicationContext 클래스를 사용하면 된다.		
		GenericApplicationContext gac2 = new GenericXmlApplicationContext("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
		Hello hellos = gac2.getBean("hello", Hello.class);
	}
	
	
	@Test
	public void simpleBeanScanning(){
		
		
		//애노테이션이 정의된 클래스를 자동으로 Scan하여 컨텍스트 생성
		ApplicationContext ctx = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
		
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		
		assertThat(hello, is(notNullValue()));
		
		//빈 메타정보를 정의한 클래스를 생성자로 받아 컨텍스트 생성
		ApplicationContext ctx2  = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
		AnnotatedHello hello2 = ctx2.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello2, is(notNullValue()));
		
		//정의한 클래스 자체르 빈 객체로 반환함.
		AnnotatedHelloConfig config = ctx2.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertThat(config, is(notNullValue()));		
	}
	
}
