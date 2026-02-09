package Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

	 private static final String URL =
		        "jdbc:oracle:thin:@localhost:1522/xepdb1";
		    private static final String USER = "RevWork";
		    private static final String PASSWORD = "rev1234";

		    public static Connection getConnection() {
		        try {
		            Class.forName("oracle.jdbc.driver.OracleDriver");
		            return DriverManager.getConnection(URL, USER, PASSWORD);
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }
		    }
}
