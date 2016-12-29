package springbook.user.sqlservice;

import javax.annotation.PostConstruct;

public class BaseSqlService implements SqlService {
	
	
	protected SqlReader sqlReader;
	
	protected SqlRegistry sqlRegistry;
	
	
	@PostConstruct
	public void loadSql(){	
		this.sqlReader.read(sqlRegistry);
				
	}
		

	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}


	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
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
	
	
	

}
