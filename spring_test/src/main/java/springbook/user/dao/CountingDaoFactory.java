package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CountingDaoFactory {
	
	@Bean
	public UserDaoJdbc userDao(){
	
		UserDaoJdbc uDao = new UserDaoJdbc();
		//uDao.setConnectionMaker(this.connectionMaker());
		
		return uDao;
	}
	
	@Bean
	public ConnectionMaker connectionMaker(){
		return new CountingConnectionMaker(this.realConnectionMaker());
	}
	
	
	@Bean
	public ConnectionMaker realConnectionMaker(){
		return new DConnectionMaker();
	}
	

}
