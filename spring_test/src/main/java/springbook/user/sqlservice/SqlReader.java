package springbook.user.sqlservice;

public interface SqlReader {

	
	//SQL을 읽어 SqlRegistry에 저장한다.
	void read(SqlRegistry sqlRegistry);
	
}
