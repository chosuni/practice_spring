package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/*
 * 다이내믹 프록시로부터 요청을 전달받으려면 InvocationHandler 인터페이스를 구현해야 한다.
 */
public class UppercaseHandler implements InvocationHandler {
	
	Hello target;
	
	
	public UppercaseHandler(Hello target){
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		
		Object ret = method.invoke(target, args);
		
		// 메소드이름이 say 로 시작하는 경우에만 적용할 경우.
		//method.getName().startsWith("say");
		
		if(ret instanceof String && method.getName().startsWith("say")){
			return ((String)ret).toUpperCase();
		} else {
			return ret;
		}
		
		
	}

}
