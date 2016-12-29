package springbook.learningtest.spring.pointcut;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class PointcutExpressionTest {
	
	
	@Test
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		
		//execution 지시자를 사용한 포인트컷 표현식		
		// Target의 minus() 메소드 시그너처
		//간략한 표현식 execution(int minus(int, int)) --> 어떤 접근자를 가졌든 어떤 클래스던, 어떤 예외를 던지던 해당 조건만 맞으면 결정되는 포인트컷
		//간략한 표현식 execution(* minus(int, int)) --> 어떤 접근자를 가졌든 어떤 클래스던, 어떤 반환값이던, 어떤 예외를 던지던 해당 조건만 맞으면 결정되는 포인트컷
		//간략한 표현식 execution(* minus(..)) --> 어떤 접근자를 가졌든 어떤 클래스던, 어떤 반환값이던, 어떤 예외를 던지던 어떤 파라미터 타입/갯수이던 해당 조건만 맞으면 결정되는 포인트컷
		//간략한 표현식 execution(* *(..))  --> 모든 클래스, 메소드의 파라미터를 허용함
		
		// 이 포인트 컷에서 클래스의 조건은 Type 조건이지 해당 클래스의 이름과 일치하는 것을 조건으로 선정하는 것이 아님(클래스명은 달라도 타입이 같으면 대상이 됨)
		pointcut.setExpression("execution(public int springbook.learningtest.spring.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");;
		
		//Target.minus();
		assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true));
		
		//Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false));
		
		
		
	}
	
	
	
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), is(expected));
	}
	
	
	public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		
		pointcutMatches(expression, expected[0], Target.class, "hello");
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		
		pointcutMatches(expression, expected[4], Target.class, "method");
		pointcutMatches(expression, expected[5], Bean.class, "method");
	}
	
	@Test
	public void pointcut() throws Exception {
		this.targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
	}

}
