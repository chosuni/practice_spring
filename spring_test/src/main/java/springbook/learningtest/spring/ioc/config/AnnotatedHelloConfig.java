package springbook.learningtest.spring.ioc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springbook.learningtest.spring.ioc.bean.AnnotatedHello;

@Configuration  //빈 설정 메타정보를 관리하는 클래스 애노테이션 <beans> 태그와 대응됨. 이 자체 클래스도 'annotatedHelloConfig' 이름으로 빈으로 등록됨.
public class AnnotatedHelloConfig {

	/**
	 * id = "annotatedHello" 인 빈 객체를 생성(등록)한다.
	 * <bean> 태그와 대응됨.
	 * @return
	 */
	@Bean
	public AnnotatedHello annotatedHello(){
		return new AnnotatedHello();
	}
}
