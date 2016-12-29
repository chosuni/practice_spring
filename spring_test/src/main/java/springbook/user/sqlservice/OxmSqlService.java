package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import springbook.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {

	//변경 불가능한 SQL Reader
	private final OxmSqlReader owmSqlReader = new OxmSqlReader();
	
	//OXM에 종속적인 SQL Reader 을 제외한 기타 다른 서비스의 기능은 BaseSqlService를 사용하기 위한 설정. (반복로직 제거)
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	
	
	//기본 SQL Registry 설정( DI를 통해 바꿀수 있음
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	
	
	//DI 받은것을 OxmSqlReader에 그대로 전달함. (직접 받을 수 없으므로)
	public void setUnmarshaller(Unmarshaller unmarshaller){
		this.owmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	//DI 받은것을 OxmSqlReader에 그대로 전달함. (직접 받을 수 없으므로)	
	public void setSqlmapFile(String sqlmapFile){
		this.owmSqlReader.setSqlmapFile(sqlmapFile);
	}
	
	
	//DI 받은것을 OxmSqlReader에 그대로 전달함. (직접 받을 수 없으므로)		
	//스프링은 xml 설정파일의 리소스 경로 설정정보를 사용하여 자동으로 Resource 객체로 반환하여 준다(스프링의 ApplicationContext에서 담당)
	public void setSqlmap(Resource sqlmap){		
		this.owmSqlReader.setSqlmap(sqlmap);
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// TODO Auto-generated method stub
		try {
			
			//공통 로직 적용.
			return this.baseSqlService.getSql(key);
		} catch(SqlNotFoundException snfe){
			throw new SqlRetrievalFailureException(snfe);
		}
	}
	
	
	
	@PostConstruct
	public void loadSql(){
		
		this.baseSqlService.setSqlReader(this.owmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		
		// SQL 초기화 Read
		this.baseSqlService.loadSql();
	}
	
	
	
	private class OxmSqlReader implements SqlReader {
		
		
		private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		
		
		//private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);
		private Resource sqlmap = new ClassPathResource("/sqlmap.xml");
							
		private Unmarshaller unmarshaller;
		
		//DI전 기본값 설정.
		private String sqlmapFile = OxmSqlReader.DEFAULT_SQLMAP_FILE;
		
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			// TODO Auto-generated method stub
			
			try {
				
				Source source = new StreamSource(sqlmap.getInputStream());
				
				//Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
				Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlmap.getSql()){
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch(IOException ioe){
				throw new IllegalArgumentException(this.sqlmap.getFilename() +"을 가져올 수 없습니다.");
			}
			
			
			
		}


		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}


		public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}


		
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}
		
		
		
		
	}

}
