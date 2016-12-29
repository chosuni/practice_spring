package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler {
	
	private Object target;   // 부가기능을 제공할 target object. 어떤 타입의 오브젝트도 적용 가능하다.
	
	private PlatformTransactionManager transactionManager;    //트랜잭션 기능을 제공하는데 필요한 트랜잭션 메니저.
	
	private String pattern;   //트랜잭션을 적용할 메소드 이름 패턴.
	
	
	public void setTarget(Object target){
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern){
		this.pattern = pattern;		
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		
		
		//트랜잭션 적용 대상을 선별하여 트랜잭션 경계설정 기능을 부여해 준다.
		if(method.getName().startsWith(this.pattern)){
			return this.invokeInTransaction(method, args);
		} else {
			return method.invoke(target, args);
		}
	}

	
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable{
		
		// 트랜잭션 시작
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			
			//타깃 객체의 메소드를 호출한다.
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
			return ret;		
			
		} catch(InvocationTargetException ite){
			
			this.transactionManager.rollback(status);
			throw ite.getTargetException();
		}
	}
	
}
