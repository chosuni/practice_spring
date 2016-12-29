package springbook.learningtest.spring.embeddeddb;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class EmbeddedDbTest {
	
	
	EmbeddedDatabase db;
	
	// JDBC Template을 더 편리하게 사용할 수 있게 확장한 템플릿
	SimpleJdbcTemplate template;
	
	@Before
	public void setUp(){
		db = new EmbeddedDatabaseBuilder()
			.setType(HSQL)
			.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql") //테이블 생성과 초기 데이터 삽입 sql 문 지정.
			.addScript("classpath:/springbook/learningtest/spring/embeddeddb/data.sql")
			.build();   // Hyper SQL 내장형 DB를 생성하고, 초기화 스크립트 및 데이터 삽입이 마무리 된 이후 해당 DB를 접근할 수 있는 DataSource를 반환함.
		
		template = new SimpleJdbcTemplate(db);		
	}
	
	
	@org.junit.After
	public void tearDown(){
		//테스트 종료 후  DB를 내림.
		db.shutdown();
	}
	
	
	// 초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트.
	@Test
	public void initData(){
		
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
		
		assertThat((String)list.get(0).get("key_"), is("KEY1"));
		assertThat((String)list.get(0).get("sql_"), is("SQL1"));
		assertThat((String)list.get(1).get("key_"), is("KEY2"));
		assertThat((String)list.get(1).get("sql_"), is("SQL2"));

	}
	
	
	@Test
	public void insert() {
		template.update("insert into sqlmap(key_, sql_) values(?, ?)", "KEY3", "SQL3");
		
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(3));
	}

}
