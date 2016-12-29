package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


/*
 * 생성할 Object 타입을 지정하지 않고, 범용으로 사용하기 위해 Object로 설정함.
 */
public class TxProxyFactoryBean implements FactoryBean<Object> {
	
	Object target;
	
	PlatformTransactionManager transactionManager;
	
	String pattern;
	
	//다이내믹 프록시를 생성할 때 필요하다. UserService외의 인터페이스를 가진 타깃에도 적용할 수 있다.
	Class<?> serviceInterface;
			

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	

	@Override
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		
		
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(this.target);
		txHandler.setTransactionManager(this.transactionManager);
		txHandler.setPattern(this.pattern);
		return Proxy.newProxyInstance(
				this.getClass().getClassLoader()
				, new Class[]{this.serviceInterface}
				, txHandler
		);
		
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		
		//팩토리 빈이 생성하는 Object 타입은 DI 받은 인터페이스 타입에 따라 달라진다.
		return this.serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
