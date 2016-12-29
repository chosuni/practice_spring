package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcContext {

	
	private DataSource dataSource;

	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	
	public void executeSql(final String query) throws SQLException {
		
		StatementStrategy strategy = new StatementStrategy(){
			
			@Override
			public PreparedStatement makePreparedStatement(Connection conn)
					throws SQLException {
				// TODO Auto-generated method stub
				return conn.prepareStatement(query);
			}
			
		};
		
		this.workWithStatementStrategy(strategy);

	}
	
	
	
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			
			conn = dataSource.getConnection();
			pstmt = stmt.makePreparedStatement(conn);

			pstmt.executeUpdate();

			pstmt.close();
			conn.close();

		} catch(SQLException e) {
			throw e;

		} finally {

			if(pstmt != null) {
				try {
					pstmt.close();
				} catch(SQLException e){

				}
			}


			if(conn != null){
				try {
					conn.close();
				} catch(SQLException e) {

				}
			}

		}

	}
	
}
