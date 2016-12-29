package springbook.user.dao;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.AppContext;
import springbook.user.service.TestAppContext;


@RunWith(SpringJUnit4ClassRunner.class)   //스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
//@ContextConfiguration(locations="/applicationContext.xml")  //테스트 컨텍스트가 자동으로 만들어 줄 애플리케이션 컨텍스트의 위치 지정.
@ActiveProfiles("test")   // AppContext에서 로딩되는 Context 중, "test" 프로파일만 사용
@ContextConfiguration(classes={AppContext.class})
public class UserDaoTest {
	
	@Autowired
	private UserDaoJdbc uDao;
		
	@Autowired
	private DataSource dataSource;
	
	
	private User user1;
	private User user2;
	private User user3;
	
	
	@Autowired  //테스트 오브젝트가 만들어지면 스프링테스트 컨텍스트에 의해 자동으로 값이 주입된다.
	ApplicationContext context = null;

	@Before
	public void setUp(){
		//ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
				
		
		this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "");
		this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "");
		this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "");		
		
		
		//강제로 DataSource 바꾸기(Spring에서 자동 셋팅된 이후)
		DataSource dataSource = new SingleConnectionDataSource("jdbc:sqlserver://localhost:1433;databaseName=springDB", "spring_user", "!welcome0", true);
		uDao.setDataSource(dataSource);
		
	}	
	
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {

		uDao.deleteAll();
		assertThat(uDao.getCount(), is(0));
				
		uDao.add(user1);
		uDao.add(user2);
		
		assertThat(uDao.getCount(), is(2));
		
	
		
		User userget1 = uDao.get(user1.getId());		
		checkSameUser(userget1, user1);
		
		User userget2 = uDao.get(user2.getId());
		checkSameUser(userget2, user2);
				
		
	}
	
	
	@Test
	public void count() throws Exception {
		


		uDao.deleteAll();
		assertThat(uDao.getCount(), is(0));
		
			
		uDao.add(user1);		
		assertThat(uDao.getCount(), is(1));

		uDao.add(user2);		
		assertThat(uDao.getCount(), is(2));
		
		uDao.add(user3);		
		assertThat(uDao.getCount(), is(3));		
	}
	
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.dao.UserDaoTest");
	}

	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException, ClassNotFoundException {
		

		uDao.deleteAll();
		
		assertThat(uDao.getCount(), is(0));
		
		uDao.get("unknown_id");
		
		
	}
	

	@Test
	public void getAll() throws SQLException, ClassNotFoundException {
		uDao.deleteAll();
		List<User> users0 = uDao.getAll();
		assertThat(users0.size(), is(0));
		
		
		uDao.add(user1);
		
		List<User> users1 = uDao.getAll();
		assertThat(users1.size(), is(1));
		this.checkSameUser(user1, users1.get(0));
		

		uDao.add(user2);
		List<User> users2 = uDao.getAll();
		assertThat(users2.size(), is(2));
		this.checkSameUser(user1, users2.get(0));  // 알파벳 순으로 정렬됨을 가정하여 index 결정 
		this.checkSameUser(user2, users2.get(1));		
	
		
		uDao.add(user3);
		List<User> users3 = uDao.getAll();
		assertThat(users3.size(), is(3));
		this.checkSameUser(user3, users3.get(0));  // 알파벳 순으로 정렬됨을 가정하여 index 결정 
		this.checkSameUser(user1, users3.get(1));				
		this.checkSameUser(user2, users3.get(2));
	}
	
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));		
	}
	
	
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey(){
		
		uDao.deleteAll();
		
		uDao.add(user1);
		uDao.add(user1);		// 강제로 동일키 insert하여 예외 생성함.
		
	}
	

	@Test
	public void sqlExceptionTranslate(){
		uDao.deleteAll();
		
		try {
			uDao.add(user1);
			uDao.add(user1);
		} catch(DuplicateKeyException dke){
			SQLException sqle = (SQLException)dke.getRootCause();  //Spring의 JDBC UnCheckedException의 JDBC SQL Exception 추출
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			//translate 메소드: SQLException이 실제 Spring에서 표현 가능한 어떤 종류의 UnCheckedException(가장 근접한) 을 발생시킴. 
			assertThat(set.translate(null, null, sqle), is(DuplicateKeyException.class));
			
		}
		
	}
	
	
	
	@Test
	public void update(){
		uDao.deleteAll();
		
		uDao.add(user1);			//수정할 사용자
		uDao.add(user2); 		//수정하지 않을 사용자
		
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		uDao.update(user1);
		
		User user1Update = uDao.get(user1.getId());
		checkSameUser(user1, user1Update);
		
		//user2는 영향이 없는지 Check(user1만 update되었는지 체크)
		User user2same = uDao.get(user2.getId());
		checkSameUser(user2, user2same);
		
	}
	
}
