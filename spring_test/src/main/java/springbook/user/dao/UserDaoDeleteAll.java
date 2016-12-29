package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoDeleteAll extends UserDaoJdbc {

	protected PreparedStatement makeStatement(Connection conn)
			throws SQLException {
		// TODO Auto-generated method stub
		return conn.prepareStatement("delete from users");
	}

}
