package springbook.issuetracker.sqlservice;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import springbook.user.sqlservice.SqlNotFoundException;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
	
	
	SimpleJdbcTemplate jdbc;
	
	//JdbcTemplate와 트랜잭션을 동기화 해 주는 트랜잭션 템플릿이다. 멀티 스레드 환경에서 공유 가능하다. 
	TransactionTemplate transactionTemplate;
		
	
/*
 * INSERT INTO SQLMAP(KEY_, SQL_) VALUES('KEY1', 'SQL1');
INSERT INTO SQLMAP(KEY_, SQL_) VALUES('KEY2', 'SQL2');
 */
	//DataSource
	public void setDataSource(DataSource dataSource) {
		jdbc = new SimpleJdbcTemplate(dataSource);
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
		transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
		
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void registerSql(String key, String sql) {
		// TODO Auto-generated method stub

		jdbc.update("insert into SQLMAP(KEY_ , SQL_) values(?, ?)", key, sql);
		
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		// TODO Auto-generated method stub
		
		try {
			
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
			
			
		} catch(EmptyResultDataAccessException e) {
			
			//쿼리의 결과가 없을경우 Exception 발생.
			throw new SqlNotFoundException(key + " 에 해당하는 SQL을 찾을 수 없습니다.");
		}	
	}

	@Override
	public void updateSql(String key, String sql)
			throws SqlUpdateFailureException {
		// TODO Auto-generated method stub

		int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
		
		if(affected == 0){
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
		}
	}

	@Override
	public void updateSql(final Map<String, String> sqlmap)
			throws SqlUpdateFailureException {
		// TODO Auto-generated method stub

		//트랜잭션 코드 추가
		transactionTemplate.execute(
				new TransactionCallbackWithoutResult() {
					 
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						
						for(Map.Entry<String, String> entry: sqlmap.entrySet()) {
							updateSql(entry.getKey(), entry.getValue());
						}
					}
										
				} );	
	}

}
