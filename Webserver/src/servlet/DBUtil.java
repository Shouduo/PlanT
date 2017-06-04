package servlet;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
 
public class DBUtil {
	//table
	public static final String TABLE_HOURLY_DATA = "table_hourly_data";
	public static final String TABLE_DAILY_DATA = "table_daily_data";
	
	private static final String server = "localhost";
	private static final String port = "3306";
	private static final String dbName = "plant";
	private static final String user = "Shouduo";
	private static final String password = "91726313GG";
	private static final String useSSL = "true";
	private static final String driverName = "com.mysql.jdbc.Driver";
	
	//connect to MySql database
	public static Connection getConnect() {
		String url = "jdbc:mysql://" + server + ":" + port + "/" + dbName
				+ "?user=" + user + "&password=" + password + "&useSSL=" + useSSL;
		Connection connector = null;
		try {
			Class.forName(driverName); //
			connector = (Connection) DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());  
            System.out.println("SQLState: " + e.getSQLState());  
            System.out.println("VendorError: " + e.getErrorCode()); 
		}
		return connector;
	}
}
