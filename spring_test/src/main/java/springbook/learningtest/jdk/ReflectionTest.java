package springbook.learningtest.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;



import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ReflectionTest {

	
	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";
		
		//length()
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		
		//charAt()
		assertThat(name.charAt(0), is('S'));
		
		Method chatAtMethod = String.class.getMethod("charAt", int.class);
		assertThat((Character)chatAtMethod.invoke(name, 0), is('S'));
	}
	
	
	@Test
	public void simpleProxy(){
		Hello hello = new HelloTarget();   
		
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
		//Dynamic Proxy를 사용
		// 생성되는 다이네믹 프록시가 클라이언트로 부터 받는 모든 요청은 Invoke() 메소드로 전달된다. 
		// 구현할 인터페이스의 각 메소드는 UppercaseHandler의 invoke 메소드로 실행된다는 의미임.
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				this.getClass().getClassLoader()  //동적으로 생성되는 다이네믹 프록시 클래스의 로딩에 사용할 클래스 로더.
				, new Class[]{Hello.class}  // 구현할 인터페이스. 다이네믹 프록시는 한번에 하나 이상의 인터페이스를 구현할 수 도 있다.
				, new UppercaseHandler(new HelloTarget())  //부가 기능과 위임코드(HelloTarget 객체)를 담은 Invocation Handler 클래스.
		);
		
		
		
		/*
		 * Target을 직접 주입하여 만든 Proxy
		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		 */
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
		

		
		
		
	}
	
	
	
	
}
