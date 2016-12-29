package springbook.user.sqlservice;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader {
	
	
	
	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";

	
	//만약 DI를 통해 sqlmapFile이 주입되지 않을 경우 사용할 기본 xml 파일.
	private String sqlmapFile = JaxbXmlSqlReader.DEFAULT_SQLMAP_FILE ;
			
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
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
