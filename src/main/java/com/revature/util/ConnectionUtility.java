package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.mariadb.jdbc.Driver;

public class ConnectionUtility {

	private ConnectionUtility() {	
	}
	
	public static Connection getConnection() throws SQLException {
		
		DriverManager.registerDriver(new Driver());
		
		
		String url = System.getenv("db_url");
		String username = System.getenv("db_username");
		String password = System.getenv("db_password");
		
		Connection connection = DriverManager.getConnection(url, username, password);
		
		
		return connection;
	}
	
}
