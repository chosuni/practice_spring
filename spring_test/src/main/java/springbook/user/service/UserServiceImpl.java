package springbook.user.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;


//@Component  //Bean 자동 등록.
@Service("userService")   //서비스 계층을 나타내는 Bean 자동등록 애노테이션. 이 에노테이션은 비즈니스 로직을 담고있는 서비스 계층의 빈이라고 식별하기 위해 사용함.
public class UserServiceImpl implements UserService {
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	@Autowired
	UserDao userDao;
	
	//TX를 지원하는 Connection Manager
	//private PlatformTransactionManager transactionManager;
		
	
	@Autowired
	private MailSender mailSender;
	
	
	
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}


	/*
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	*/


	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
	
	
	public void add(User user){
		
		if(user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}		
		userDao.add(user);
		
	}
	
	/**
	 * JDBC 템플릿에서 사용할 Connection 동기화 적용.
	 * @throws Exception
	 */
	public void upgradeLevels() {
		
		
		
		List<User> users = userDao.getAll();
		
		for(User user : users) {
			if(canUpgradeLevel(user)){	
				
				upgradeLevel(user);
			}		
		}		
		
		
		
		//트랜잭션 시작(TransactionStatus 객체 생성)
		//TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); 
		
		
		/*//동기화 시작.
		TransactionSynchronizationManager.initSynchronization();
			
		//Connection 객체를 생성함과 동시에 트랜잭션 동기화에 사용하도록 저장소에 바인딩까지 해줌.
		Connection conn = DataSourceUtils.getConnection(dataSource);
		conn.setAutoCommit(false);*/

		/*
		try {
			
			//트랜잭션 코드와 Business 로직과의 분리.
			this.upgradeLevelsInternal();
			
			// TX Commit
			transactionManager.commit(status);
				
		//	conn.commit();
		} catch(Exception e){
			

			//TX Rollback
			transactionManager.rollback(status);
		//	conn.rollback();
			throw e;
		} finally {

			
			//Spring Method를 통해 Connection 객체를 닫음.
			DataSourceUtils.releaseConnection(conn, dataSource);
			
			//동기화 작업 종료 및 정리.
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}

		*/
	}
	

	private void upgradeLevelsInternal(){
		
		List<User> users = userDao.getAll();
		
		for(User user : users) {
			if(canUpgradeLevel(user)){	
				
				upgradeLevel(user);
			}		
		}
	}
	
	
	private boolean canUpgradeLevel(User user){
		
		Level currentLevel = user.getLevel();
		switch(currentLevel){
		
		case BASIC:
			return (user.getLogin() >= UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER);
			
		case SILVER:
			return (user.getRecommend() >= UserServiceImpl.MIN_RECCOMEND_FOR_GOLD);
			
		case GOLD:
			return false;
			
		default:
			throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		
		}
	}
	
	
	protected void upgradeLevel(User user){
		user.upgradeLevel();
		userDao.update(user);
		
		this.sendUpgradeEMail(user);
	}	
	
	
	
	private void sendUpgradeEMail(User user){		
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		//mailMessage.setTo(user.getEmail());
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("mesarang79@naver.com");
		
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
		
		mailSender.send(mailMessage);
		
		
	}	
	

	
	
	
	@Override
	public User get(String id) {
		// TODO Auto-generated method stub
		return userDao.get(id);
	}


	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return userDao.getAll();
	}


	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		userDao.deleteAll();
	}


	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		userDao.update(user);
	}

























	//트랜잭션 테스트를 위한 중첩 클래스
	public static class TestUserService extends UserServiceImpl {
		
		//예외를 발생시킬 User Object의 id (static class를 bean에 추가할 것이므로 직접 하드코딩)
		//private String id;
		
		private String id = "madnite1";  //  users(3)의 id 값을 고정시켜 버렸다.
		
		/*
		public TestUserServiceImpl(String id){
			this.id = id;
		}
		*/
		
		
		protected void upgradeLevel(User user){
			if(user.getId().equals(this.id)) {
				throw new TestUserServiceException();  //예외를 강제로 발생시켜서 레벨 업그레이드 작업을 중단함. 
			}
			super.upgradeLevel(user);
		}				
		
		
		public List<User> getAll(){
			
			for(User user : super.getAll()){
				super.update(user);  // read-only 트랜잭션에 강제로 쓰기 시도를 한다.
			}
			return null;
		}
		
		
	}		
	
	static class TestUserServiceException extends RuntimeException {
		
	}
	
	

	
	
}
