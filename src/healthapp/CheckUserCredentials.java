package healthapp;

import java.sql.CallableStatement;
import java.sql.SQLException;

public class CheckUserCredentials {
    /**
     * Verifies the PID, password and role of the given user.
     * */
    public static boolean verifyLogin(String role, String pid, String password) {
	String sql = "{ ? = call validate_health_supporter (?,?) }";
	long verificationFlag = -1;
	if (role.equalsIgnoreCase("Patient")) {
	    sql = "{ ? = call validate_patient (?,?) }";
	}
	CallableStatement statement;
	try {
	    statement = DatabaseOperations.connection.prepareCall(sql);
	    statement.registerOutParameter(1, java.sql.Types.INTEGER);
	    statement.setString(2, pid);
	    statement.setString(3, password);
	    statement.execute();
	    verificationFlag = statement.getInt(1);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return verificationFlag == 1;
    }
}