package springbook.user.service;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;









import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserServiceImpl.TestUserServiceException;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")  // AppContext에서 로딩되는 Context 중, "test" 프로파일만 사용
@ContextConfiguration(classes= {AppContext.class})  // Context Annotation을 사용한 클래스 설정파일로 컨텍스트 초기화.
//@ContextConfiguration(locations="/test-applicationContext.xml")  설정 파일로 컨텍스트 초기화
    //GenericApplicationContext 클래스가 해당 locations 속성에 있는 xml을 사용하여 컨텍스트 객체를 생성하고, 초기화 한다.
//@Transactional
//@TransactionConfiguration(defaultRollback=false)//클래스 전역 트랜잭션 속성 설정.(디폴트 트랜잭션 메니저는 관례를 따라 id가 transactionManager 로 등록된 트랜잭션 빈을 사용함) 세부적인 예외 사항은 각 메소드에서 지정하면 됨.  
public class UserServiceTest {
	
	@Autowired
	DefaultListableBeanFactory dlbf;     //스프링 컨테이너가 빈을 등록하고 관리하기 위한 팩토리 클래스. 이 빈 팩토리를 자동으로 주입 받을 수 있다.(스프링이 주입해 준다.)
	
	@Autowired //@Autowired는 기본적으로 type-driven injection 이다. 타입으로 참조할 빈을 찾았을 때 같은 타입의 빈이 여러 개 검색되었을 경우, 이름을 기준으로 최종 후보를 찾는 방식이지만, @Qualifier annotation을 사용하여 구분할 수 있도록 해준다.   
	PlatformTransactionManager transactionManager;
		
	@Autowired
	UserService userService;	
	
	@Autowired(required = false)  //필수적으로 DI를 하지 않음(타입이 같은 경우로 자동으로 DI를 할 수 없을 경우에 임시방편으로...)
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserService testUserService;   
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	UserDao userDao;
	
	
	@Autowired
	MailSender mailSender;
	
	List<User> users;
	
	@Test
	public void bean(){
		assertThat(this.userService, is(notNullValue()));
	}
	
	
	@Before
	public void setup(){
		users = Arrays.asList(
					new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "")
					, new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "")
					, new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "")
					, new User("madnite1", "이상호", "p4", Level.SILVER , 60, MIN_RECCOMEND_FOR_GOLD, "")
					, new User("green", "오민규", "p5", Level.GOLD , 100, Integer.MAX_VALUE, "")									
				);
												
	}
	
	/**
	 * Mock Dao를 사용한 테스트 케이스
	 * @throws Exception
	 */
	@Test
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao); //Mock Object 만든 User-Dao를 직접 DI 해준다.
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated(); //내부적으로 upgradeLevel() 호출된 User 개체를 List 형태로 보관하고 이를 반환한다.
		assertThat(updated.size(), is(2));
		this.checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		this.checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		
		
		
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	
	/**
	 * Mockito Mock Framework를 사용한 (고립된) 테스트.
	 * @throws Exception
	 */
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//UserDao Mock 클래스 생성 (빈 Mock 객체), Mock Class는 각 메소드를 호출한 기록을 가지고 있다.
		UserDao mockUserDao = mock(UserDao.class);
		
		//Mock 클래스 반환값 설정: getAll() 메소드 호출시 이 객체의 users를 반환하도록 설정
		when(mockUserDao.getAll()).thenReturn(this.users);
		
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		
		userServiceImpl.upgradeLevels();
		
		
		//mockUserDao 객체의 update() 메소드가 2번 호출되었는지 검증(User 객체를 파라미터로 받는 Update 메소드를 2번 호출했는지) any()는 파라미터 내용은 무시하고 호출 횟수만 확인 가능함.
		verify(mockUserDao, times(2)).update(any(User.class));   
		
		verify(mockUserDao).update(users.get(1));  //users의 두번째 개체를 파라미터로 Update 메소드가 호출된 적이 있는지 검증한다(파라미터 내용 점검)
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		
		verify(mockUserDao).update(users.get(3)); //users의 네번째 개체를 파라미터로 Update 메소드가 호출된 적이 있는지 검증한다(파라미터 내용 점검)
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessage = mailMessageArg.getAllValues();
		
		assertThat(mailMessage.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessage.get(1).getTo()[0], is(users.get(3).getEmail()));
		
	}
	
	
	/*
	@Test
	@DirtiesContext   //Context의 DI 설정을 변경하는 테스트라는 것을 알려준다.
	public void upgradeLevels() throws Exception{
		userDao.deleteAll();
		
		for(User user : users) {
			userDao.add(user);
		}
		
		
		MockMailSender mockMailSender = new MockMailSender();
		
		//수동으로 DI(설정상은 DummyMailSender 임)
		userServiceImpl.setMailSender(mockMailSender);
		
		userService.upgradeLevels();
		
		//결과 Check
		checkLevelUpgraded(users.get(0), false); // 업데이트가 안되었을 것이다.
		checkLevelUpgraded(users.get(1), true); // 업데이트가 되었을 것이다
		checkLevelUpgraded(users.get(2), false); // 업데이트가 안되었을 것이다.
		checkLevelUpgraded(users.get(3), true); // 업데이트가 되었을 것이다.
		checkLevelUpgraded(users.get(4), false); // 업데이트가 안되었을 것이다.
								
		//업데이트가 된 사람에게 메일을 전송하고 그 정보를 검증한다.( Mock Object를 사용한 테스트)
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		
	}
	
	*/
	
	
	private void checkLevel(User user, Level expectedLevel){
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	
	
	
	/**
	 * 
	 * @param user 초기 user 객체
	 * @param upgraded 어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드를 할 것인가를 지정함.
	 */
	private void checkLevelUpgraded(User user, boolean upgraded){
		
		User userUpdate = userDao.get(user.getId()); //DB기준으로 데이터 Refresh(업데이트 되었다면 된 값을 가져올 것임) 
		
		
		if(upgraded){
			// 업그레이드가 일어 났는지 확인
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			// 업그레이드가 일어 나지 않았는지 확인.
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	
	@Test
	public void add(){
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);  // GOLD 레벨
		
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);  //레벨 삭제.
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevel.getLevel(), is(userWithLevelRead.getLevel()));
		assertThat(userWithoutLevel.getLevel(), is(userWithoutLevelRead.getLevel()));		
	}
	
	
	@Test
	//@DirtiesContext
	public void upgradeAllOrNothing() throws Exception{

		
		//@autowired로 static class bean을 찾는 코드로 변경하였으므로 아래 코드는 주석.
		//TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId()); //User 3이 실패되면, 			
		//testUserService.setUserDao(userDao);
//		testUserService.setTransactionManager(transactionManager);
		//testUserService.setMailSender(mailSender);
		
		
		//Spring Bean을 사용한 Dynamic Proxy 트랜잭션 관리
		//여기서는 TestUserService를 테스트 용으로 target를 사용해야 하므로 applicationContext.xml에서 설정한 target을 강제로 대체하기 위한 코드(실제는 이렇게 하지 않아도 됨)
		
		//팩토리빈 자체를 가져옴
		//TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
		//ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
		//txProxyFactoryBean.setTarget(testUserService); //테스트를 위한 서비스 객체의 DI 변경.
		
		//UserService txUserService = (UserService)txProxyFactoryBean.getObject();  //변경된 Target을 사용하여 Proxy 방법의 UserService 객체를 새롭게 생성한다.
		
		
		
		//Dynamic Proxy를 사용한 트랜잭션 관리
		/*
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(testUserService);
		txHandler.setPattern("upgradeLevels"); // upgradeLevels 만 트랜잭션 관리를 함.
		txHandler.setTransactionManager(this.transactionManager);
		
		UserService txUserService = (UserService)Proxy.newProxyInstance(
				this.getClass().getClassLoader()
				, new Class[]{UserService.class}
				, txHandler
		);
		*/
		
				
		
		/*
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		*/
		
		
		
		userDao.deleteAll();
		
		for(User user : users){
			userDao.add(user);
		}
		
		try {
			this.testUserService.upgradeLevels();
			
			/*
			 * 
			 * 
			 * 테스트가 위 문장을 만나면 message를 출력하고 무조건 실패하도록 한다.
			 * 위 메써드는 주로 예외상황을 테스트하거나 아직 테스트가 끝나지 않았음을 명시적으로 나타내주기 위해 자주 사용되곤 한다.
			 * 혹시라도 테스트 코드를 잘못 작성해서 예외가 발생되지 않았을 경우를 가정하여 코드를 추가함.
			 */
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException tuse){
			
		}
		
		checkLevelUpgraded(users.get(1), false);  // User 1도 같이 실패가 되는지 Test
		
		
		
		
	}

	
	static class MockMailSender implements MailSender {
		
		private List<String> requests = new ArrayList<String>();
		
		/*
		 * UserService로부터 전송 요청을 받은 메일 주소를 저장해 두고, 이를 읽을 수 있게 한다.
		 */
		public List<String> getRequests(){
			return this.requests;
		}
		

		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			// TODO Auto-generated method stub
					
			requests.add(mailMessage.getTo()[0]);   //전송 요청을 받은 이메일 주소를 저장(첫번째 To);
			
		}

		@Override
		public void send(SimpleMailMessage[] mailMessage) throws MailException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	static class MockUserDao implements UserDao {
		
		private List<User> users;  // 개별 업그레이드 후보 User Object 등록
		private List<User> updated = new ArrayList<User>();   //업데이트 될 Data를 저장함.
		

		/**
		 * 주어진 변수를 테스트 대상 User Data로 설정하여 객체를 생성한다.
		 * @param users
		 */
		private MockUserDao(List<User> users){
			this.users = users;
		}
		
		
		public List<User> getUpdated() {
			return updated;
		}

		
		@Override
		public void add(User user) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public User get(String id) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public List<User> getAll() {
			// TODO Auto-generated method stub
			return this.users;
		}

		@Override
		public void deleteAll() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		
		@Override
		public void update(User user) {
			// TODO Auto-generated method stub
			this.updated.add(user);			
		}
						
	}
	
	
	/*
	 * 자동 프록시 생성기로 만들어 Bean에 자동 주입된 객체(Proxy Bean)는 Proxy 클래스의 서브 클래스임.  
	 */
	@Test
	public void advisorAutoProxyCreator(){
		
		assertThat(testUserService, is(Proxy.class));
	}


	
	/*
	 * 
	 * advisor에서 DataSourceTransactionManager을 사용하여 트랜잭션을 관리할 때, read-only="true" 로 설정한 경우, 얻어지는 Connection 객체의 setReadOnly(true)로 설정되나 사용하는
	 * JDBC 드라이버에서 이를 지원하지 않아 Exception이 발생되지 않을 수 있다.
	 * 
	 * MS SQL Server의 경우, 해당 모드를 지원하지 않아 readonly 모드로 설정하더라도 Exception이 발생되지 않는다. 
	 * https://msdn.microsoft.com/ko-kr/library/ms378818(v=sql.110).aspx
	 * 
	 * @Test(expected=TransientDataAccessResourceException.class)
	 */
	@Test
	public void readOnlyTransactionAttribute(){
		testUserService.getAll();
	}
	

	@Test
	public void transactionSync(){
		//트랜잭션이 3개 발생됨. UserService 인터페이스에 @Transactional 애노테이션을 기본값으로 사용 (기본 트랜잭션 전파속성: REQUIRED)
		// 트랜잭션의 경계점은 UserService 인터페이스임.
		
		userService.deleteAll();
		
		userService.add((users.get(0)));
		userService.add((users.get(1)));
		
		
		//UserService에 REQUIRED 기본 트랜잭션 전파속성이지만 하나의 트랜잭션으로 묶어 버릴경우는 아래와 같이 작성
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		txDefinition.setReadOnly(true);
		
		// 새로운 트랜잭션이 시작됨.
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
		
		
		//트랜잭션의 사용
		userService.deleteAll();		
		userService.add((users.get(0)));
		userService.add((users.get(1)));		
		
		
		//앞에서 시작한 트랜잭션을 커밋한다.
		transactionManager.commit(txStatus);
		
		
		//롤백 테스트
		//데이터 초기화
		userService.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		
		 txDefinition = new DefaultTransactionDefinition();
		 txDefinition.setReadOnly(true);
			
		 // 새로운 트랜잭션이 시작됨.
		 txStatus = transactionManager.getTransaction(txDefinition);
		
		userService.add((users.get(0)));
		userService.add((users.get(1)));		
		assertThat(userDao.getCount(), is(2));
		
		transactionManager.rollback(txStatus);
		assertThat(userDao.getCount(), is(0));
		
		
	}
	
	
	//테스트에 사용된 @Transactional은 테스트가 끝나면 자동으로 롤백된다.
	
	@Test
	@Transactional(readOnly=true) // 클래스 및 메소드 레벨에 적용 가능
	@Rollback(true)  // Transaction의 자동 Rollback 제어를 위해 사용함. 기본값은 true이며 롤백을 하지 않을려면 false로 설정함. 각 메소드 레벨에만 적용 가능함. 클래스에 설정된 @TransactionConfiguration 보다 우선 적용됨.
	public void transactionSync2(){
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));

	}
	
	@Test
	public void Beans(){
		for(String n: dlbf.getBeanDefinitionNames()) {
			System.out.println(n + "\t " + dlbf.getBean(n).getClass().getName());
		}
	}
	
	
}

