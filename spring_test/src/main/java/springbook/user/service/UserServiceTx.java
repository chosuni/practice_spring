package springbook.user.service;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

public class UserServiceTx implements UserService {

	UserService userService;
	PlatformTransactionManager transactionManager;
	
	

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void add(User user) {
		// TODO Auto-generated method stub

		userService.add(user);
		
	}

	/*
	 * 트랜잭션을 별도로 분리한 UserService 구현 클래스.
	 * (non-Javadoc)
	 * @see springbook.user.service.UserService#upgradeLevels()
	 */	
	@Override
	public void upgradeLevels() {
		// TODO Auto-generated method stub
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			
			userService.upgradeLevels();
			
			this.transactionManager.commit(status);
		} catch(RuntimeException re){
			this.transactionManager.rollback(status);
			throw re;
		}

	}

	@Override
	public User get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
