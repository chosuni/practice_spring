package springbook.learningtest.jdk.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import springbook.learningtest.jdk.Hello;
import springbook.learningtest.jdk.HelloTarget;
import springbook.learningtest.jdk.UppercaseHandler;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DynamicProxyTest {
	
	@Test
	public void simpleProxy(){
		
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				this.getClass().getClassLoader()
				, new Class[]{Hello.class}
				, new UppercaseHandler(new HelloTarget())
		);
				
	}
	
	@Test
	public void proxyFactoryBean(){
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		
		/*
		 * 타깃 설정, 이 타깃 객체를 사용하여 Proxy 객체 타입을 추출하고 이를 모두 구현한 객체를 반환할 수 있게 된다(Proxy 타입 클래스 자동 추출 기능)
		 * setInterface() 메소드를 사용하여 Proxy 타입 클래스를 명시적으로 지정해 줄 수 있다. 
		 */
		pfBean.setTarget(new HelloTarget()); 
		
		
		/*
		 * 부가기능을 담은 Advice를 추가한다. 여러개를 추가할 수 있으며, 여기서는 타깃에 대문자로 전환하는 기능을 추가하였다.
		 * MethodInterceptor 인터페이스는 Advice 인터페이스를 상속하고 있음
		 * 타깃 오브젝트에 적용하는 부가기능을 담은 오브젝트를 스프링에서는 Advice라고 부른다.
		 */
		pfBean.addAdvice(new UppercaseAdvice());  
		
		Hello proxiedHello = (Hello)pfBean.getObject();  // Proxy 생성한 객체를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		
	}
	
	
	static class UppercaseAdvice implements MethodInterceptor{

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// TODO Auto-generated method stub
			
			
			
			String ret = (String)invocation.proceed(); // 리플렉션의 Method 객체와는 달리, 메소드 실행시 타깃 오브젝트를 전달할 필요가 없다. MethodInvocation은 메소드 정보와 함께 타깃 객체를 알고 있기 때문이다.
			
			return ret.toUpperCase();
			
		}
		
	}
	
	@Test
	public void pointcutAdvisor(){
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		//mappedName 속성값을 이용해 메소드의 이름을 비교하는 방식을 지원함.
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");  // sayH로 시작하는 모든 메소드를 선택함.
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));  //해당 포인트 컷과 advice를 묶어서 Advisior 타입으로 한번에 추가한다.
		//pfBean.addAdvice(advice); 포인트 컷이 없을 경우는 addAdvice 메소드를 사용함.
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));   //PointCut의 조건에 맞지 않아 소문자로 출력됨.(기본 타겟을 사용함.)
		
		
	}
	
	
	
	@Test
	public void classNamePointcutAdvisor(){
		
		//포인트컷 준비(익명 내부 클래스 방법을 사용함
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){

			@Override
			public ClassFilter getClassFilter() {
				// TODO Auto-generated method stub
				return new ClassFilter(){

					@Override
					public boolean matches(Class<?> clazz) {
						// TODO 클래스 이름이 HelloT 로 시작하는 것만 적용되도록 한다.						
						return clazz.getSimpleName().startsWith("HelloT");
					}					
					
				};
			}								
			
		};
		
		classMethodPointcut.setMappedName("sayH*");
		
		//테스트 ( 적용 대상 클래스임)
		this.checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		//테스트(적용대상 클래스가 아님)
		class HelloWorld extends HelloTarget {};  //타겟을 상속
		this.checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		//테스트(적용 대상 클래스임)
		class HelloToby extends HelloTarget {}; //타겟을 상속
		this.checkAdviced(new HelloToby(), classMethodPointcut, true);
		
	}
	
	
	/**
	 * 
	 * @param target
	 * @param pointcut
	 * @param adviced 적용 대상인지 여부
	 */
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		
		if(adviced){			
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
			
		} else {
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));			
		}
	}

}
