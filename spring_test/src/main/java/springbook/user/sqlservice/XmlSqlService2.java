package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService2 implements SqlService, SqlRegistry, SqlReader{
	
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	private SqlReader sqlReader;
	
	private SqlRegistry sqlRegistry;
	
	private String sqlmapFile;
	
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	
	

	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	
	
	@PostConstruct
	public void loadSql(){
		this.sqlReader.read(this.sqlRegistry);
	}
	

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		// TODO Auto-generated method stub
		try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException snfe){
			throw new SqlRetrievalFailureException(snfe);
		}
	}

	@Override
	public void registerSql(String key, String sql) {
		// TODO Auto-generated method stub
		
		this.sqlMap.put(key, sql);
		
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		
		if(sql == null){
			throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}
	}

	@Override
	public void read(SqlRegistry sqlRegistry) {
		// TODO Auto-generated method stub
		
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
				
			//JAXB 컴파일러가 생성해 준 클래스 패키지로 Context를 생성해야 한다.(언마샬링 대상 객체를 정의한 패키지)
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlMapData = (Sqlmap)unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlMapData.getSql()){
				
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
			
		} catch(JAXBException jaxbe){
			throw new RuntimeException(jaxbe);			
		}		
		
		
		
	}

	
	
	
}
