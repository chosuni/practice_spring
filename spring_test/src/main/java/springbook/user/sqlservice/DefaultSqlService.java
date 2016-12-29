package springbook.user.sqlservice;

public class DefaultSqlService extends BaseSqlService {
	
		
	/*
	 * 생성자에서 디폴트 의존 객체를 직접 DI하여 필요없는 Bean 설정을 방지함.
	 * 
	 * 거의 DI가 변경이 없을 서비스 빈일 경우, 추가적으로 만들어서 사용함.
	 */
	public DefaultSqlService(){
		this.setSqlReader(new JaxbXmlSqlReader());
		this.setSqlRegistry(new HashMapSqlRegistry());
	}

}
