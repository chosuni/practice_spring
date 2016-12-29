package springbook.user.sqlservice.updatable;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.TransactionException;

import springbook.issuetracker.sqlservice.EmbeddedDbSqlRegistry;
import springbook.issuetracker.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

	EmbeddedDatabase db;
	
	
	//UpdatableSqlRegistry를 사용하는 공통 테스트 케이스를 위한 객체 생성.
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		// TODO Auto-generated method stub
		db = new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql")
			.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	
	@After
	public void tearDown(){
		db.shutdown();
	}
	
	
	@Test
	public void transactionalUpdate()  {
		
		//초기상태 확인.
		this.checkFind("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY9999!@#!", "Modified9999");
		
		try {
			
			sqlRegistry.updateSql(sqlmap);			
			this.fail();			
		} catch(TransactionException sufe){
			
			//에러가 나면...해당 에러된 상태로 머물게 된다. (단건단건 transaction)
			sufe.printStackTrace();
		}
		
		//트랜잭션이 설정되지 않은경우, 에러나는 것이 정상임.
		this.checkFind("SQL1", "SQL2", "SQL3");
		
	}
	
	private void fail(){
		
	}

}
