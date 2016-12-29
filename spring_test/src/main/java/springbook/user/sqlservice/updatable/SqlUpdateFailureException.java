package springbook.user.sqlservice.updatable;

public class SqlUpdateFailureException extends RuntimeException {
	
	public SqlUpdateFailureException(String message){
		super(message);
	}
	
	
	public SqlUpdateFailureException(String message, Throwable t){
		super(message, t);
	}

}
