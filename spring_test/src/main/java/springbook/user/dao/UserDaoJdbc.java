package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.sqlservice.SqlService;



//자동으로 Bean 등록하여 관리함.(id=userDao 로).. 생략하면 클래스명의 첫문자를 소문자를 가지는 id를 가짐.
//@Component("userDao")

//DAO를 자동으로 Bean으로 등록하기 위한 에노테이션 (Component를 사용해도 되지만..) @Repository는 @Component를 매타 에노테이션으로 가지고 있다.
@Repository("userDao")
public class UserDaoJdbc implements UserDao{

	@Autowired
	private SqlService sqlService;
	
	private JdbcTemplate jdbcTemplate;
	
	// SQL문을 Spring 설정파일에서 주입받을 변수
	//private Map<String, String> sqlMap;

	//DI로 값이 설정되긴 하지만, 타 클래스에서 강제적으로 설정할 수도 있으므로 Open.
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}


	
	/*
	 * SqlService DI로 대체.
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	*/


	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rownum) throws SQLException {
			// TODO Auto-generated method stub
			
			User user = new User(rs.getString("id")
										, rs.getString("name")
										, rs.getString("password")
										, Level.valueOf(rs.getInt("level"))
										, rs.getInt("login")
										, rs.getInt("recommend")
										, rs.getString("email")
					);			
			return user;	
		}				
	};
	
	
	
	
	/*
	 * 메소드에 @Autowired 에노테이션이 설정되면, 파라미터 타입을 보고 주입가능한 빈을 모두 찾는다. DataSource 타입을 빈을 찾아 스프링이 수정자 메소드를 호출해서 넣어준다.
	 * 타입이 존재하는 빈의 2개 이상일 경우, 그중에서 프로퍼티와 동일한 이름의 빈이 있는지 찾고 수정자 프로퍼티와 이름이 일치하는 Bean id를 찾아 이를 자동으로 주입해 준다.
	 */
	@Autowired 
	public void setDataSource(DataSource dataSource) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);		
	}


	public void add(final User user) {
		
		//"insert into users(id, name, password, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd"), user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
				
	}
	
	
	public User get(String id) {			
				
		//"select * from users where id = ?" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet") , new Object[]{id}, this.userMapper);  // 쿼리는 1개의 값을 반환하도록 기대하고 있으며 0일 경우는 Exception 발생.		
	}
	

	public List<User> getAll(){
		/*
		 * Query 템플릿은 SQL문을 실행해서 얻은 ResultSet의 모든 Row를 열람하면서 row마다 RowMapper Callback을 호출함.
		 * Query 템플릿은 SQL문 결과 길이가 0이어도 Exception을 발생시키지는 않는다. 대신 크기가 0인 List<T>를 반환한다.
		 * "select * from users order by id" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		 */
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
	}

	
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=springDB;user=spring_user;password=!welcome0");
		
		return conn;
	}
	
	
	
	
	
	public void deleteAll(){
	
		//"delete from users" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		String sqlDelete = this.sqlService.getSql("userDeleteAll");
		
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {  // 매개변수가 없는 SQL문을 실행할때 사용하는 CallBack Class 

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				// TODO Auto-generated method stub
				return conn.prepareStatement("delete from users");
			}
			
		};
		
		//this.jdbcTemplate.update(psc);  // CallBack 객체를 넘기는 경우.
		this.jdbcTemplate.update(sqlDelete);  // 바로 SQL 문을 넘기는 경우
	}
	

	
	
	public int getCount() {
		
		//"select count(*) from users" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		String sqlGetCount=this.sqlService.getSql("userGetCount");
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				// TODO Auto-generated method stub
				return conn.prepareStatement("select count(*) from users");
			}
			
		};
				
		ResultSetExtractor<Integer> rse = new ResultSetExtractor<Integer> () {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				// TODO Auto-generated method stub
				rs.next();				
				return rs.getInt(1);
			}
			
		};
				
		//return this.jdbcTemplate.query(psc, rse);
		
		return this.jdbcTemplate.queryForInt(sqlGetCount);
		

	}


	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		
		//"update users set name = ?, password = ?, level = ?, login = ?, recommend  = ?, email = ? where id = ?" 문을 스프링 설정 파일에서 주입받은 변수로 대체
		
		this.jdbcTemplate.update(this.sqlService.getSql("userUpdate")
				, user.getName()
				, user.getPassword()
				, user.getLevel().intValue()
				, user.getLogin()
				, user.getRecommend()
				, user.getEmail()				
				, user.getId()
				);
		
		
		
	}
				
	
}
