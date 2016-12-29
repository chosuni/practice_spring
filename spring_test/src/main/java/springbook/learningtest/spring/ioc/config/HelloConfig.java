package springbook.learningtest.spring.ioc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;

@Configuration
public class HelloConfig {

	@Bean
	public Hello hello(){
		Hello hello = new Hello();
		hello.setName("Spring");
		hello.setPrinter(printer());
		return hello;		
	}
	
	@Bean
	public Hello hello2(){
		
		Hello hello = new Hello();
		hello.setName("Spring2");
		
		/*
		 * // 스프링 컨테이너가 이 HelloConfig을 메타정보로 사용하는 경우에는 
		 * printer() 메소드를 여러번 호출해도 매번 동일한 StringPrinter 객체를 돌려받는다(기본값은 싱글톤 임, @Bean 애노테이션이 기본값에 따라 메소드를 조작함.)
		 * 		 * 단 @Configuration 에노테이션이 사용된 클래스일 때만 적용됨( 메소드에 @Bean 만 사용할 경우는 늘 새로운 객체가 반환됨. 
		 * 
		 */
		hello.setPrinter(printer());  
		return hello;
		
	}
	
	@Bean
	public Printer printer(){
		return new StringPrinter();
	}
	
}
