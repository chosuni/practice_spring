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

public class XmlSqlService implements SqlService {
	
	
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	private String sqlmapFile;
	
	
	
	
	
	public XmlSqlService(){
		

		
	}
	
	
	
	/*
	 * @PostConstruct : 스프링은 XmlSqlService 클래스로 등록된 빈의 Object를 생성하고, DI 작업을 마친 뒤, 이 메소드를 자동으로 실행해 준다. 
	 */
	@PostConstruct
	public void loadSql(){
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
				
			//JAXB 컴파일러가 생성해 준 클래스 패키지로 Context를 생성해야 한다.(언마샬링 대상 객체를 정의한 패키지)
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlMapData = (Sqlmap)unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlMapData.getSql()){
				
				sqlMap.put(sql.getKey(), sql.getValue());
			}
			
		} catch(JAXBException jaxbe){
			throw new RuntimeException(jaxbe);			
		}
	}
	
	
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		
		// TODO Auto-generated method stub
		String sql = sqlMap.get(key);
		
		if(sql == null){
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		} else {
			return sql;
		}
	}

	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	

}
