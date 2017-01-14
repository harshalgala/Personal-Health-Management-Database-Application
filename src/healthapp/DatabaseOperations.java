package healthapp;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseOperations {

    static Connection connection = null;

    /**
     * Attempts to establish a connection to the given database URL. 
     * Reference: Oracle docs.
     * @return true if connection is successful. false otherwise.
     * */
    public static boolean isConnectionSuccessful() {
	try {
	    DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	try {
	    connection = DriverManager.getConnection("jdbc:oracle:thin:@orca.csc.ncsu.edu:1521/orcl.csc.ncsu.edu",
		    "sjha5", "200157082");
	    return true;
	} catch (SQLException e) {
	    System.out.println("\nConnection to the database has failed!\n");
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * Executes the given SQL statement, which may be an INSERT, UPDATE, or
     * DELETE statement or an SQL statement that returns nothing, such as an SQL
     * DDL statement. Reference: Oracle docs.
     * 
     * @return either (1) the row count for SQL Data Manipulation Language (DML)
     *         statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public static int executeUpdate(String query) throws SQLException {
	Statement statement = connection.createStatement();
	statement.setQueryTimeout(10);
	// System.out.println("\nQUERY: " + query);
	int updateResult = statement.executeUpdate(query);
	return updateResult;
    }

    /**
     * Executes the given SQL statement, which returns a single ResultSet
     * object. Reference: Oracle docs.
     * 
     * @return ResultSet object that contains the data produced by the given
     *         query; never null
     */
    public static ResultSet executeQuery(String query) {
	ResultSet resultSet = null;
	try {
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(10);
	    // System.out.println("\nQUERY: " + query);
	    resultSet = statement.executeQuery(query);
	    return resultSet;
	} catch (SQLException e) {
	    e.printStackTrace();
	    return resultSet;
	}
    }

    /**
     * Releases this Connection object's database and JDBC resources immediately
     * instead of waiting for them to be automatically released. Reference:
     * Oracle docs.
     */
    public static void closeConnection() {
	try {
	    if (connection != null) {
		connection.close();
		System.out.println("\nConnection has been successfully closed");
	    } else {
		System.out.println("\nConnection was already closed.");
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}