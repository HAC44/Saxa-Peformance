package DbConnection;

import java.sql.Connection;


import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHandler extends Configs {
	
	Connection dbconnection;
	
	public DbHandler() {
		
	}
	
	public Connection getConnection() {
		
		String connectionString = "jdbc:mysql://" + this.dbhost + ":" + this.dbport + "/" + this.dbname;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			dbconnection = DriverManager.getConnection(connectionString, this.dbuser, this.dbpass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dbconnection;
	}
}
