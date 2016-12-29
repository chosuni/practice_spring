package springbook.learningtest.jdk.proxy;

import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {

	
	public void setMappedClassName(String mappedClassName){
		this.setClassFilter(new SimpleClassFilter(mappedClassName));
	}
	
	
	static class SimpleClassFilter implements ClassFilter {
		
		String mappedName;
		
		private SimpleClassFilter(String mappedName){
			this.mappedName = mappedName;
		}

		@Override
		public boolean matches(Class<?> clazz) {
			// TODO Auto-generated method stub
			
			//와일드카드(*)가 들어간 문자열 비교를 지원하는 유틸리티 메소드. *name, name*, *name* 모두 지원한다. mappedName의 유형이 두번째 인자에 해당하는지 여부를 반환함.
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
		}
		
	}
	
	

}
