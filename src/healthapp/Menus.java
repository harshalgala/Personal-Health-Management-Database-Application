package healthapp;

import java.io.Console;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Menus {

    static Scanner scanner = new Scanner(System.in);
    static String pid;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    static String presentSystemDate = dateFormat.format(Calendar.getInstance().getTime()).toString();

    /**
     * Displays the entry menu after verifying user.
     */
    static void displayEntryMenu() {
	String title = "\nWelcome to Personal Health Management (PHM) Database Application!".toUpperCase();
	System.out.println("");
	for (int i = 0; i < title.length() - 1; i++) {
	    System.out.print("=");
	}
	System.out.println(title);
	for (int i = 0; i < title.length() - 1; i++) {
	    System.out.print("=");
	}
	System.out.println("\n");
	int choice = 0;
	do {
	    System.out.println("Choose one of the following:\n" + "1. Login\n" + "2. Sign Up\n" + "3. Exit");
	    System.out.print("Enter your choice (1-3): ");
	    choice = getUserChoice(scanner.nextLine(), 3);
	    switch (choice) {
	    case 1:
		displayLoginPage();
		break;
	    case 2:
		displaySignUpPage();
		break;
	    case 3:
		displayExitPage();
		break;
	    }
	} while (true);
    }

    /**
     * Displays menu for signing up.
     */
    private static void displaySignUpPage() {
	System.out.println("Sign Up Page\n");
	System.out.print("Enter your PID: ");
	String peopleid = scanner.nextLine();
	System.out.print("Enter the first name: ");
	String firstName = scanner.nextLine();
	System.out.print("Enter your last name: ");
	String lastName = scanner.nextLine();
	System.out.print("Enter your date of birth (DD-MMM-YYYY): ");
	String dob = scanner.nextLine();
	System.out.print("Enter your gender (M/F): ");
	String gender = scanner.nextLine().toLowerCase();
	System.out.print("Enter your address: ");
	String address = scanner.nextLine();
	System.out.print("Enter your contact number: ");
	String contactno = scanner.nextLine();
	System.out.print("Enter your password: ");
	String password = scanner.nextLine();
	System.out.print("Enter your registration date [" + Menus.presentSystemDate + "]: ");
	String registrationDate = scanner.nextLine();
	if (registrationDate == null || registrationDate.length() == 0) {
	    registrationDate = Menus.presentSystemDate;
	}
	System.out.print("Do you want to be added as patient (Y/N)? ");
	String userInput1 = scanner.nextLine().toLowerCase();
	int patientFlag = 0;
	if (userInput1.equals("y") || userInput1.equals("yes")) {
	    patientFlag = 1;
	}
	System.out.print("Do you want to be added as health supporter (Y/N)? ");
	String userInput2 = scanner.nextLine();
	int hsFlag = 0;
	if (userInput2.equalsIgnoreCase("y") || userInput2.equalsIgnoreCase("yes")) {
	    hsFlag = 1;
	}

	String query = "INSERT INTO people (pid, first_name, last_name, date_of_birth, gender, address, "
		+ "contact_number, password, registration_date, patient_flag, health_supporter_flag) " + "VALUES ('"
		+ peopleid + "','" + firstName + "','" + "','" + lastName + "','" + dob + "','" + gender + "','"
		+ address + "'," + contactno + ",'" + password + "','" + registrationDate + "'," + patientFlag + ","
		+ hsFlag + ")";

	try {
	    if (DatabaseOperations.executeUpdate(query) == 1) {
		System.out.println("\nSign up successful.\n");
	    }
	} catch (Exception e) {
	    System.out.println("Probem with sign up");
	    if (e.getMessage().contains("unique constraint")) {
		System.out.println("PID Repeated. Enter a different PID");
	    } else if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
		System.out.println("The month count should be between the 1st and last day of the month");
	    } else if (e.getMessage().contains("literal does not match format string")) {
		System.out.println("Error in the date of birth entered");
	    } else if (e.getMessage().contains("not a valid month")) {
		System.out.println("The month added is invalid");
	    } else if (e.getMessage().contains("check constraint")) {
		System.out.println("Select the gender properly either M/F");
	    } else if (e.getMessage().contains("invalid number")) {
		System.out.println("The input required is only numbers, no other characters allowed");
	    } else if (e.getMessage().contains("value too large for column")) {
		System.out.println("Number of characters entered is too long");
	    } else {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Displays menu for login page.
     */
    private static void displayLoginPage() {
	Menus.pid = "";
	int attempts = 0;
	boolean exitThisMenu = false;
	int maxAttempt = 3;
	String role = "";
	do {
	    int choice = 0;
	    System.out.println("\nPlease enter your role:\n" + "1. Patient\n" + "2. Health Supporter");
	    System.out.print("Enter your choice (1-2): ");
	    choice = getUserChoice(scanner.nextLine(), 2);
	    if (choice != -1) {
		attempts = 0;
		switch (choice) {
		case 1:
		    role = "Patient";
		    break;
		case 2:
		    role = "HS";
		    break;
		}
		attempts++;
		System.out.print("\nEnter your PID: ");
		String userInputPID = scanner.nextLine();
		Console console = System.console();
		System.out.print("Enter your password: ");
		char[] passwordInput = console.readPassword();
		String password = String.valueOf(passwordInput);
		boolean verifyLogin = CheckUserCredentials.verifyLogin(role, userInputPID, password);
		if (verifyLogin) {
		    attempts = 0;
		    Menus.pid = userInputPID;
		    if (role.equals("Patient")) {
			System.out.println("\nEntering as patient.\n");
			displayPatientMainMenu();
		    } else if (role.equals("HS")) {
			System.out.println("\nEntering as health supporter.\n");
			displayHealthSupporterMainMenu();
		    } else {
			System.out.println("Your role has not been defined. Please contact admin to add roles.");
		    }
		} else {
		    System.out.println("Incorrect username/password or role");
		}
	    } else {
		attempts++;
	    }
	    if (attempts > maxAttempt) {
		System.out.println("\nMaximum attempt exceeded.\n");
		exitThisMenu = true;
	    }
	} while (!exitThisMenu);
	displayEntryMenu();
    }

    /**
     * Displays exit menu. After confirmation: closes connection, scanner and
     * then exits.
     */
    private static void displayExitPage() {
	System.out.println("\nAre you sure you want to exit?");
	System.out.print("Press N to return to previous menu, anything else to exit: ");
	if (!scanner.nextLine().equalsIgnoreCase("n")) {
	    DatabaseOperations.closeConnection();
	    System.out.println("\nExiting the application now.");
	    scanner.close();
	    System.exit(0);
	} else {
	    System.out.println("\nReturning to previous menu.\n");
	}
    }

    /**
     * Displays alert for particular patient.
     * <p>
     * <b>Note:</b> If health supporter is calling this function, only those
     * data whose recorded date is after start date of health supporter will be
     * displayed.
     * 
     * @param patientId
     *            PID of the patient whose alerts are to be viewed.
     */
    private static void displayAlertsPage(String patientId) {
	try {
	    // delete alerts
	    DatabaseOperations.executeUpdate("delete from recorded_alerts where patient_pid='" + patientId + "'");

	    // Fetching HID for calling outside_limit_alert procedure
	    String queryForHidDate = "SELECT hof.health_obs_id, p.registration_date"
		    + " FROM health_obs_frequency hof, people p WHERE hof.patient_pid ='" + patientId + "' AND p.pid ='"
		    + patientId + "' UNION SELECT dr.health_obs_id, d.diagnosis_date"
		    + " FROM disease_recommendations dr, diagnosis d WHERE dr.disease_id = d.disease_id"
		    + " AND d.patient_pid ='" + patientId + "'";
	    Map<Integer, Date> mapOfHidDate = new HashMap<Integer, Date>();
	    ResultSet resultSetForHidDate = DatabaseOperations.executeQuery(queryForHidDate);
	    while (resultSetForHidDate.next()) {
		mapOfHidDate.put(resultSetForHidDate.getInt(1), resultSetForHidDate.getDate(2));
	    }

	    for (int hid : mapOfHidDate.keySet()) {
		String queryForLowActivityAlert = "{ call low_activity_alert (?, ?, ?) }";
		CallableStatement statementForLowActivityAlert = DatabaseOperations.connection
			.prepareCall(queryForLowActivityAlert);
		System.out.println("PID: " + patientId);
		System.out.println("HID: " + hid);
		System.out.println("Date: " + mapOfHidDate.get(hid).toString());
		statementForLowActivityAlert.setString(1, patientId);
		statementForLowActivityAlert.setInt(2, hid);
		statementForLowActivityAlert.setDate(3, mapOfHidDate.get(hid));
		try {
		    statementForLowActivityAlert.setQueryTimeout(10);
		    statementForLowActivityAlert.execute();
		} catch (SQLTimeoutException e) {
		    System.out.println(
			    "Timeout Exception for low activity alert trigger." + " Data shown may be incomplete.");
		} catch (SQLException e) {
		    System.out.println("Exception for low activity alert trigger." + " Data shown may be incomplete.");
		    e.printStackTrace();
		}

		// Calling procedure outside_limit_alert for each HID
		String queryForOutsideLimitAlert = "{ call outside_limit_alert (?, ?) }";
		CallableStatement statementForOutsideLimitAlert = DatabaseOperations.connection
			.prepareCall(queryForOutsideLimitAlert);
		statementForOutsideLimitAlert.setString(1, patientId);
		statementForOutsideLimitAlert.setInt(2, hid);
		try {
		    statementForOutsideLimitAlert.setQueryTimeout(10);
		    statementForOutsideLimitAlert.execute();
		} catch (SQLTimeoutException e) {
		    System.out.println(
			    "Timeout Exception for outside limit alert trigger." + " Data shown may be incomplete");
		} catch (SQLException e) {
		    System.out.println("Exception for low activity alert trigger." + " Data shown may be incomplete");
		    e.printStackTrace();
		}
	    }

	    // Display alerts
	    int i = 0;
	    String healthObservation = "Health_Observation";
	    String alertType = "Alert_Type";
	    String recordedDate = "Recorded_Date";
	    String queryForDisplayingAlerts = "SELECT h.name AS " + healthObservation + ", a.name AS " + alertType
		    + ", ra.recorded_date AS " + recordedDate
		    + " FROM health_observation h, alert a, recorded_alerts ra"
		    + " WHERE a.id = ra.alert_id AND h.id = ra.health_obs_id AND ra.patient_pid ='" + patientId
		    + "' ORDER BY " + healthObservation + "," + recordedDate;
	    if (!Menus.pid.equals(patientId)) {
		queryForDisplayingAlerts = "SELECT h.name AS " + healthObservation + ", a.name AS " + alertType
			+ ", ra.recorded_date AS " + recordedDate
			+ " FROM health_observation h, alert a, recorded_alerts ra"
			+ " WHERE a.id = ra.alert_id AND h.id = ra.health_obs_id AND ra.patient_pid ='" + patientId
			+ " 'AND ra.recorded_date>=(SELECT start_date from support" + " WHERE patient_pid='" + patientId
			+ "' AND health_supporter_pid='" + Menus.pid + "' ORDER BY " + healthObservation + ","
			+ recordedDate;
	    }
	    ResultSet resultSetForAlerts = DatabaseOperations.executeQuery(queryForDisplayingAlerts);
	    if (resultSetForAlerts.next()) {
		System.out.println(
			String.format("%3s %-30s %-20s %-20s", "No.", healthObservation, alertType, recordedDate));
		do {
		    i++;
		    String valHealthObservation = resultSetForAlerts.getString(healthObservation);
		    String valAlertType = resultSetForAlerts.getString(alertType);
		    String valDate = resultSetForAlerts.getDate(recordedDate).toString();
		    System.out.println(
			    String.format("%3d %-30s %-20s %-20s", i, valHealthObservation, valAlertType, valDate));
		} while (resultSetForAlerts.next());
	    } else {
		System.out.println("\nNo alerts to display./n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Displays all the health supporters for patient.
     */
    private static void displayHealthSupportersPageForPatient() {
	System.out.println();
	HashMap<String, String> hsPidMap = new HashMap<String, String>();
	String query = "SELECT p.pid, p.first_name, p.last_name, s.health_supporter_type, s.start_date AS Authorization_Date "
		+ "FROM people p, support s WHERE p.pid = s.health_supporter_pid AND s.patient_pid ='" + Menus.pid
		+ "' ORDER BY s.health_supporter_type";

	try {
	    ResultSet resultSet = DatabaseOperations.executeQuery(query);
	    if (resultSet.next()) {
		System.out.println();
		System.out.println(String.format("%4s %-20s %-15s %-15s %-15s", "PID", "HealthSupporterType",
			"FirstName", "LastName", "StartDate"));
		do {
		    String valID = resultSet.getString("pid");
		    String valFirstName = resultSet.getString("first_name");
		    String valLastName = resultSet.getString("last_name");
		    String valHSType = resultSet.getString("health_supporter_type");
		    String valStartDate = resultSet.getDate("Authorization_Date").toString();
		    System.out.println(String.format("%5s %-20s %-15s %-15s %-15s", valID, valHSType, valFirstName,
			    valLastName, valStartDate));
		    hsPidMap.put(valHSType, valID);
		} while (resultSet.next());
	    } else {
		System.out.println("\nNo health supporters to display.\n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	int choice = 0;
	do {
	    System.out.println("1. Add/remove health supporters");
	    System.out.println("2. Edit health supporters details");
	    System.out.println("3. Go back to previous menu");
	    System.out.println("4. Log out");
	    System.out.println("5. Exit");

	    System.out.print("Enter your choice (1-5): ");
	    choice = getUserChoice(scanner.nextLine(), 5);
	    switch (choice) {
	    case 1:
		displayAddRemoveHSPage();
		break;
	    case 2:
		displayHSEditProfilePage();
		break;
	    case 3:
		displayPatientMainMenu();
		break;
	    case 4:
		displayEntryMenu();
		break;
	    case 5:
		displayExitPage();
		break;
	    }
	} while (true);
    }

    /**
     * Displays main menu for patient.
     */
    private static void displayPatientMainMenu() {
	int choice = 0;
	do {
	    int addHealthSupporter = -1;
	    CallableStatement callableStmt = null;
	    String call = "{? = call sick_needs_health_supporter(?)}";
	    do {
		try {
		    callableStmt = DatabaseOperations.connection.prepareCall(call);
		    callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
		    callableStmt.setString(2, Menus.pid);
		    callableStmt.execute();
		    if (callableStmt.getInt(1) == 1) {
			System.out.println("Add a primary health supporter first. Then only you can access full menu.");
			displayAddRemoveHSPage();
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    } while (addHealthSupporter == 1);

	    System.out.println("\nChoose one of the following: ");
	    System.out.println("1. Profile");
	    System.out.println("2. Health Supporter");
	    System.out.println("3. Health Indicators");
	    System.out.println("4. Alerts");
	    System.out.println("5. Diagnosis");
	    System.out.println("6. Recommendation");
	    System.out.println("7. Display Recordings");
	    System.out.println("8. Logout");
	    System.out.println("9. Exit");
	    System.out.print("Enter your choice (1-9): ");
	    choice = getUserChoice(scanner.nextLine(), 9);
	    switch (choice) {
	    case 1:
		displayProfileMenu(Menus.pid);
		break;
	    case 2:
		displayHealthSupportersPageForPatient();
		break;
	    case 3:
		displayHealthIndicators(Menus.pid);
		break;
	    case 4:
		displayAlertsPage(Menus.pid);
		break;
	    case 5:
		displayDiagnosisData();
		break;
	    case 6:
		displayRecommendation();
		break;
	    case 7:
		displayRecordedValues(Menus.pid);
		break;
	    case 8:
		displayEntryMenu();
		break;
	    case 9:
		displayExitPage();
		break;
	    }
	} while (true);
    }

    /**
     * Displays all the recommendations for the patient.
     */
    private static void displayRecommendation() {
	int i = 0;
	try {
	    ResultSet resultSet = DatabaseOperations.executeQuery(
		    "SELECT * FROM health_observation WHERE id IN (SELECT DISTINCT health_obs_id FROM health_obs_frequency "
			    + "WHERE patient_pid = '" + Menus.pid
			    + "' UNION SELECT DISTINCT dr.health_obs_id FROM disease_recommendations dr, diagnosis d "
			    + "WHERE dr.disease_id = d.disease_id AND d.patient_pid = '" + Menus.pid + "')");
	    HashMap<Integer, Integer> healthRecoMap = new HashMap<Integer, Integer>();
	    if (resultSet.next()) {
		System.out.println(String.format("%15s %15s %15s %15s %15s %15s", "HealthObsID", "HealthObsName",
			"Description", "DataType", "LowerLimit", "UpperLimit"));
		do {
		    int valID = resultSet.getInt("id");
		    String valName = resultSet.getString("name");
		    String valDescription = resultSet.getString("description");
		    String valDataType = resultSet.getString("data_type");
		    int valLowerLimit = resultSet.getInt("lower_limit");
		    int valUpperLimit = resultSet.getInt("upper_limit");
		    System.out.println(String.format("%15d %15s %15s %15s %15s %15s", valID, valName, valDescription,
			    valDataType, valLowerLimit, valUpperLimit));
		    healthRecoMap.put(i++, valID);
		} while (resultSet.next());
	    } else {
		System.out.println("\nNo recommendations to display.\n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Enter the following to add: ");
	System.out.println("1. Add Patient Specific Observation Frequency");
	System.out.println("2. Add Patient Specific Limits");
	System.out.println("3. Go back to previous menu");
	System.out.print("Enter your choice (1-3): ");
	int userChoice = getUserChoice(scanner.nextLine(), 3);
	switch (userChoice) {
	case 1:
	    try {
		System.out.print("Enter the Health Observation ID: ");
		String health_obs_id = scanner.nextLine();
		System.out.print("Enter the Patient Specific Frequency: ");
		String obsFreq = scanner.nextLine();
		DatabaseOperations.executeUpdate("INSERT INTO health_obs_frequency VALUES ('" + Menus.pid + "',"
			+ health_obs_id + "," + obsFreq + ")");
	    } catch (Exception e) {
		if (e.getMessage().contains("unique constraint")) {
		    System.out.println("Health Supporter ID Repeated. Enter a different Health Supporter");
		} else if (e.getMessage().contains("invalid number")) {
		    System.out.println("The input required is only numbers no other characters");
		} else {
		    e.printStackTrace();
		}
	    }
	    break;
	case 2:
	    try {
		System.out.print("Enter the Health Observation ID: ");
		String health_obs_id = scanner.nextLine();
		System.out.print("Enter the Patient Lower Limit: ");
		int lowerLimit = scanner.nextInt();
		System.out.print("Enter the Patient Upper Limit: ");
		int upperLimit = scanner.nextInt();
		scanner.nextLine();
		DatabaseOperations.executeUpdate(
			"INSERT INTO patient_health_obs_limits (patient_pid, health_obs_id, lower_limit, upper_limit)"
				+ "VALUES ('" + Menus.pid + "'," + health_obs_id + "," + lowerLimit + "," + upperLimit
				+ ")");
	    } catch (Exception e) {
		if (e.getMessage().contains("unique constraint")) {
		    System.out.println("Health Supporter ID Repeated. Enter a different Health Supporter");
		} else if (e.getMessage().contains("invalid number")) {
		    System.out.println("The input required is only numbers no other characters");
		} else {
		    e.printStackTrace();
		}
	    }
	    break;
	case 3:
	    System.out.println("Returning to previous menu.");
	    displayPatientMainMenu();
	    break;
	}
    }

    /**
     * Displays all the diagnosis data for the patient.
     */
    private static void displayDiagnosisData() {
	ResultSet rs = DatabaseOperations
		.executeQuery("SELECT id, name, diagnosis_date FROM disease, diagnosis WHERE disease.id = "
			+ "diagnosis.disease_id AND diagnosis.patient_pid ='" + Menus.pid + "'");
	try {
	    if (rs.next()) {
		System.out.println(Menus.pid + "'s Diagnosis Information:\n");
		do {
		    System.out.println("Disease ID: " + rs.getInt("id"));
		    System.out.println("Disease Name: " + rs.getString("name"));
		    System.out.println("Diagnosis Date: " + rs.getDate("diagnosis_date") + "\n");
		} while (rs.next());
	    }
	    System.out.print("Do you want to edit your diseases (Y/N): ");
	    String userInput = scanner.nextLine();
	    if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
		System.out.println("\n1. Add new disease\n2. Remove disease\n3. Goto previous menu");
		System.out.print("Enter your choice (1-3): ");
		int userEditChoice = getUserChoice(scanner.nextLine(), 3);
		if (userEditChoice == 1) {
		    System.out.println("Choose disease to add from following list:");
		    HashMap<Integer, Integer> diseaseMap = new HashMap<Integer, Integer>();
		    ResultSet ros = DatabaseOperations.executeQuery(
			    "SELECT id, name from disease WHERE id NOT IN (SELECT DISTINCT disease_id FROM diagnosis WHERE patient_pid='"
				    + Menus.pid + "')");
		    int i = 1;
		    if (ros.next()) {
			System.out.println(String.format("%5s %-15s", "No.", "Name"));
			do {
			    int valID = ros.getInt("id");
			    String valName = ros.getString("name");
			    System.out.println(String.format("%4d %-15s", i, valName));
			    diseaseMap.put(i++, valID);
			} while (ros.next());
		    }
		    System.out.print("Enter the disease you want to add: ");
		    int userEnteredAddition = getUserChoice(scanner.nextLine(), diseaseMap.size());
		    System.out.print("Enter the start date [" + presentSystemDate + "]: ");
		    String startDate = scanner.nextLine();
		    if (startDate == null || startDate.length() == 0) {
			startDate = presentSystemDate;
		    }
		    int rowsAffected = -1;
		    try {
			rowsAffected = DatabaseOperations.executeUpdate(
				"INSERT INTO diagnosis (patient_pid, disease_id, diagnosis_date) VALUES ('" + Menus.pid
					+ "','" + diseaseMap.get(userEnteredAddition) + "','" + startDate + "')");
		    } catch (Exception e) {
			if (e.getMessage().contains("unique constraint")) {
			    System.out.println("Disease ID Repeated. Enter a different Disease ID");
			}
			if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
			    System.out.println("The month count should be between the 1st and last day of the month");
			} else if (e.getMessage().contains("literal does not match format string")) {
			    System.out.println("Some error in the date of birth entered");
			} else if (e.getMessage().contains("not a valid month")) {
			    System.out.println("The month added is invalid");
			} else {
			    e.printStackTrace();
			}
		    }
		    if (rowsAffected == 1) {
			System.out.println("\nData added");
		    } else {
			System.out.println("\nData not added, try again");
		    }
		}
		if (userEditChoice == 2) {
		    System.out.print("Enter the Disease ID you want to remove: ");
		    int userEnteredDeletion = scanner.nextInt();
		    scanner.nextLine();
		    int rowsDeleted = -1;
		    try {
			rowsDeleted = DatabaseOperations.executeUpdate("DELETE FROM diagnosis WHERE patient_pid = '"
				+ Menus.pid + "' AND disease_id = " + userEnteredDeletion);
		    } catch (Exception e) {
			if (e.getMessage().contains("unique constraint")) {
			    System.out.println("Disease ID Repeated. Enter a different Disease ID");
			}
			if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
			    System.out.println("The month count should be between the 1st and last day of the month");
			} else if (e.getMessage().contains("literal does not match format string")) {
			    System.out.println("Some error in the date of birth entered");
			} else if (e.getMessage().contains("not a valid month")) {
			    System.out.println("The month added is invalid");
			} else {
			    e.printStackTrace();
			}
		    }
		    if (rowsDeleted == 1) {
			System.out.println("Data deleted");
		    } else {
			System.out.println("Data not deleted, try again");
		    }
		}
		if (userEditChoice == 3) {
		    System.out.println("Redirecting to previous menu");
		    displayPatientMainMenu();
		}
	    } else {
		System.out.println("You have not edited anything. Redirecting you to previous menu.");
		displayPatientMainMenu();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Displays options for adding/removing health supporters.
     */
    private static void displayAddRemoveHSPage() {
	do {
	    System.out.println("\n1. Add primary supporter");
	    System.out.println("2. Remove primary supporter");
	    System.out.println("3. Add secondary  supporter");
	    System.out.println("4. Remove secondary supporter");
	    System.out.println("5. Go back to previous menu");
	    System.out.print("Enter your choice (1-5): ");
	    String userInput = scanner.nextLine();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	    String presentSystemDate = dateFormat.format(Calendar.getInstance().getTime()).toString();
	    int choice = getUserChoice(userInput, 5);
	    switch (choice) {
	    case 1:
		System.out.print("Enter the ID of new primary health supporter: ");
		String primaryId = scanner.nextLine();
		System.out.print("Enter the start date [" + presentSystemDate + "]: ");
		String startDateForPrimary = scanner.nextLine();
		if (startDateForPrimary == null || startDateForPrimary.length() == 0) {
		    startDateForPrimary = presentSystemDate;
		}
		try {
		    int primaryHsAdded = DatabaseOperations
			    .executeUpdate("insert into support (patient_pid, health_supporter_pid, start_date,"
				    + "health_supporter_type) VALUES ('" + Menus.pid + "','" + primaryId + "','"
				    + startDateForPrimary + "','primary')");
		    if (primaryHsAdded == 1) {
			System.out.println("Primary health supporter added successfully.\n");
		    } else {
			System.out.println("Error adding primary health supporter.\n");
		    }
		} catch (Exception e) {
		    if (e.getMessage().contains("unique constraint")) {
			System.out.println("Health Supporter PID Repeated. Enter a different Health Supporter PID");
		    } else if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
			System.out.println("The month count should be between the 1st and last day of the month");
		    } else if (e.getMessage().contains("literal does not match format string")) {
			System.out.println("Some error in the date of birth entered");
		    } else if (e.getMessage().contains("not a valid month")) {
			System.out.println("The month added is invalid");
		    } else {
			e.printStackTrace();
		    }
		}
		break;
	    case 2:
		try {
		    int primaryHsDeleted = DatabaseOperations.executeUpdate("delete from support where patient_pid='"
			    + Menus.pid + "' and health_supporter_type='primary'");
		    if (primaryHsDeleted == 1) {
			System.out.println("Primary health supporter deleted successfully.\n");
		    } else {
			System.out.println("Error deleted primary health supporter.");
		    }
		    String queryForSwappingHS = "{? = call update_sec_to_primary (?) }";
		    CallableStatement statementForSwappingHS = DatabaseOperations.connection
			    .prepareCall(queryForSwappingHS);
		    statementForSwappingHS.registerOutParameter(1, java.sql.Types.INTEGER);
		    statementForSwappingHS.setString(2, Menus.pid);
		    statementForSwappingHS.execute();
		    int swappingHappened = statementForSwappingHS.getInt(1);
		    if (swappingHappened == 1) {
			System.out.println("Secondary health supporter has been updated to primary health supporter.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		break;
	    case 3:
		System.out.print("Enter the ID of new secondary health supporter: ");
		String secondaryHsId = scanner.nextLine();
		System.out.print("Enter the start date [" + presentSystemDate + "]: ");
		String startDateForSecondary = scanner.nextLine();
		if (startDateForSecondary == null || startDateForSecondary.length() == 0) {
		    startDateForSecondary = presentSystemDate;
		}
		try {
		    int secondaryHsAdded = DatabaseOperations
			    .executeUpdate("insert into support (patient_pid, health_supporter_pid, start_date,"
				    + "health_supporter_type) VALUES ('" + Menus.pid + "','" + secondaryHsId + "','"
				    + startDateForSecondary + "','secondary')");
		    if (secondaryHsAdded == 1) {
			System.out.println("Secondary health supporter added successfully.\n");
		    } else {
			System.out.println("Error adding secondary health supporter.");
		    }

		} catch (Exception e) {
		    if (e.getMessage().contains("unique constraint")) {
			System.out.println("Health Supporter PID Repeated. Enter a different Health Supporter PID");
		    }
		    if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
			System.out.println("The month count should be between the 1st and last day of the month");
		    } else if (e.getMessage().contains("literal does not match format string")) {
			System.out.println("Some error in the date of birth entered");
		    } else if (e.getMessage().contains("not a valid month")) {
			System.out.println("The month added is invalid");
		    } else {
			e.printStackTrace();
		    }
		}
		break;
	    case 4:
		try {
		    int secondaryHsDeleted = DatabaseOperations.executeUpdate("delete from support where patient_pid='"
			    + Menus.pid + "' and health_supporter_type='secondary'");
		    if (secondaryHsDeleted == 1) {
			System.out.println("Secondary health supporter deleted successfully.\n");
		    } else {
			System.out.println("Error deleting secondary health supporter.");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		break;
	    case 5:
		displayHealthSupportersPageForPatient();
		break;
	    }
	} while (true);

    }

    /**
     * Displays options for editing health supporter's profile. To be done by
     * the patient.
     */
    private static void displayHSEditProfilePage() {
	HashMap<String, String> hsPidMap = new HashMap<String, String>();
	String query = "SELECT p.pid, p.first_name, p.last_name, s.health_supporter_type, s.start_date AS Authorization_Date "
		+ "FROM people p, support s WHERE p.pid = s.health_supporter_pid AND s.patient_pid ='" + Menus.pid
		+ "' ORDER BY s.health_supporter_type";
	try {
	    ResultSet resultSet = DatabaseOperations.executeQuery(query);
	    while (resultSet.next()) {
		hsPidMap.put(resultSet.getString("health_supporter_type"), resultSet.getString("pid"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	System.out.println("1. Edit primary supporter's details");
	System.out.println("2. Edit secondary supporter's details");
	System.out.print("Enter your choice (1-2): ");
	String userInput2 = scanner.nextLine();
	int choice2 = getUserChoice(userInput2, 2);
	switch (choice2) {
	case 1:
	    if (hsPidMap.get("primary") != null) {
		displayProfileMenu(hsPidMap.get("primary"));
	    } else {
		System.out.println("No primary supporter defined.");
	    }
	    break;
	case 2:
	    if (hsPidMap.get("secondary") != null) {
		displayProfileMenu(hsPidMap.get("secondary"));
	    } else {
		System.out.println("No secondary supporter defined.");
	    }
	    break;
	}
    }

    /**
     * Displays profile and editing options for a person.
     * 
     * @param pid
     *            PID of the person whose profile is to be displayed.
     */
    private static void displayProfileMenu(String pid) {
	ResultSet rs = DatabaseOperations.executeQuery("SELECT pid, first_name, last_name, date_of_birth, "
		+ "gender, address,contact_number, registration_date, patient_flag FROM people where pid='" + pid
		+ "'");
	try {
	    while (rs.next()) {
		System.out.println(rs.getString("first_name") + "'s Profile Information:\n");
		System.out.println("ID: " + rs.getString("pid"));
		if (rs.getInt("patient_flag") == 1) {
		    String sql = "{ ? = call check_sick (?) }";
		    int isSick = -1;
		    CallableStatement callableStmt = null;
		    try {
			callableStmt = DatabaseOperations.connection.prepareCall(sql);
			callableStmt.registerOutParameter(1, java.sql.Types.INTEGER);
			callableStmt.setString(2, Menus.pid);
			callableStmt.execute();
			isSick = callableStmt.getInt(1);
		    } catch (SQLException e) {
			e.printStackTrace();
		    }
		    if (isSick == 0) {
			System.out.println("\nCategory: Well patient");
		    } else {
			System.out.println("\nCategory: Sick patient");
		    }
		}
		System.out.println("\n1. First Name: " + rs.getString("first_name"));
		System.out.println("2. Last Name: " + rs.getString("last_name"));
		System.out.println("3. Date Of Birth: " + rs.getDate("date_of_birth"));
		System.out.println("4. Address: " + rs.getString("address"));
		System.out.println("5. Gender: " + rs.getString("gender"));
		System.out.println("6. Contact Number: " + rs.getInt("contact_number"));
	    }
	    System.out.print("Do you want to edit (Y/N): ");
	    String userInput = scanner.nextLine();
	    if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
		System.out.print("Enter your choice (For multiple edits, enter numbers separated by ,): ");
		String userEditChoice = scanner.nextLine();
		// Added this line because input with space (5, 6) were not
		// working
		userEditChoice = userEditChoice.replaceAll(" ", "");
		String[] choices = userEditChoice.split(",");
		LinkedList<String> listOfChoices = new LinkedList<String>(Arrays.asList(choices));
		String updateValues = "";
		if (listOfChoices.contains("1")) {
		    System.out.print("Enter your new first name: ");
		    String firstName = scanner.nextLine();
		    updateValues += "first_name='" + firstName + "'" + ",";
		}
		if (listOfChoices.contains("2")) {
		    System.out.print("Enter your new last name: ");
		    String lastName = scanner.nextLine();
		    updateValues += "last_name='" + lastName + "'" + ",";
		}
		if (listOfChoices.contains("3")) {
		    System.out.print("Enter your new date of birth: ");
		    String dob = scanner.nextLine();
		    updateValues += "date_of_birth='" + dob + "'" + ",";
		}
		if (listOfChoices.contains("4")) {
		    System.out.print("Enter your new address: ");
		    String address = scanner.nextLine();
		    updateValues += "address='" + address + "'" + ",";
		}
		if (listOfChoices.contains("5")) {
		    System.out.print("Enter your new gender: ");
		    String gender = scanner.nextLine();
		    if (gender.equalsIgnoreCase("female")) {
			gender = "f";
		    } else if (gender.equalsIgnoreCase("male")) {
			gender = "m";
		    }
		    updateValues += "gender='" + gender + "'" + ",";
		}
		if (listOfChoices.contains("6")) {
		    System.out.print("Enter your new contact number: ");
		    String contactNumber = scanner.nextLine();
		    updateValues += "contact_number='" + contactNumber + "'" + ",";
		}
		updateValues = updateValues.substring(0, updateValues.length() - 1);
		String query = "UPDATE people SET " + updateValues + " WHERE pid=" + "'" + Menus.pid + "'";
		if (DatabaseOperations.executeUpdate(query) == 1) {
		    System.out.println("\nUpdate successful.\n");
		} else {
		    System.out.println("\nUpdate failed. Please check profile to verify data.\n");
		}
	    } else {
		System.out.println("\nRedirecting to previous menu.");
	    }
	} catch (Exception e) {
	    System.out.println("Problem updating the profile details.");
	    if (e.getMessage().contains("day of month must be between 1 and last day of month")) {
		System.out.println("The month count should be between the 1st and last day of the month");
	    } else if (e.getMessage().contains("literal does not match format string")) {
		System.out.println("Some error in the date of birth entered");
	    } else if (e.getMessage().contains("not a valid month")) {
		System.out.println("The month added is invalid");
	    } else if (e.getMessage().contains("check constraint")) {
		System.out.println("Select the gender properly either M/F");
	    } else if (e.getMessage().contains("invalid number")) {
		System.out.println("The input required is only numbers no other characters");
	    } else if (e.getMessage().contains("value too large for column")) {
		System.out.println("Number of characters entered is too long");
	    } else {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Displays the list of patients for a health supporter
     */
    private static void displayAllPatientDetailsForHS() {
	Map<Integer, String> patientMapForHS = new HashMap<Integer, String>();
	String query = "SELECT pid, first_name, last_name FROM people p, support s "
		+ "WHERE p.pid = s.patient_pid AND s.health_supporter_pid ='" + Menus.pid + "'";
	int i = 1;
	ResultSet resultSet;
	try {
	    resultSet = DatabaseOperations.executeQuery(query);
	    if (resultSet.next()) {
		System.out.println(String.format("%4s %4s %-15s %-15s", "No", "PID", "FirstName", "LastName"));
		do {
		    String valID = resultSet.getString("pid");
		    String valFirstName = resultSet.getString("first_name");
		    String valLastName = resultSet.getString("last_name");
		    System.out.println(String.format("%4s %4s %-15s %-15s", i, valID, valFirstName, valLastName));
		    patientMapForHS.put(i++, valID);
		} while (resultSet.next());
	    }
	    int noOfRows = patientMapForHS.size();
	    if (!patientMapForHS.isEmpty()) {
		if (noOfRows == 1) {
		    System.out.print("Do you want to check details for patient " + patientMapForHS.get(1) + " (Y/N)? ");
		    String userInput = scanner.nextLine();
		    if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
			displayPatientDetailsForHS(patientMapForHS.get(1));
		    } else {
			System.out.println("\nReturning to previous menu.\n");
		    }
		} else {
		    System.out.print("\nDo you want to check details for a particular patient (Y/N)? ");
		    String userInput = scanner.nextLine();
		    if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
			do {
			    System.out.print("\nEnter your choice (1-" + noOfRows + "): ");
			    int choice = getUserChoice(scanner.nextLine(), noOfRows);
			    if (choice != -1) {
				displayPatientDetailsForHS(patientMapForHS.get(choice));
			    }
			} while (true);
		    } else {
			System.out.println("\nReturning to previous menu.\n");
		    }
		}
	    } else {
		System.out.println("No patients to display. Returning to previous menu");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Displays details of particular patient under a health supporter.
     * 
     * @param patientId
     *            PID of the patient whose details are to be displayed.
     */
    private static void displayPatientDetailsForHS(String patientId) {
	do {
	    System.out.println("1. Patient details");
	    System.out.println("2. Check alerts");
	    System.out.println("3. Check recordings");
	    System.out.println("4. Enter missing observations");
	    System.out.println("5. Acknowledge all alerts");
	    System.out.println("6. Go back to previous menu");
	    System.out.print("\nEnter your choice (1-6): ");
	    int choice = getUserChoice(scanner.nextLine(), 6);
	    if (choice != -1) {
		switch (choice) {
		case 1:
		    displayProfileMenu(patientId);
		    break;
		case 2:
		    displayAlertsPage(patientId);
		    break;
		case 3:
		    displayRecordedValues(patientId);
		    break;
		case 4:
		    displayHealthIndicators(patientId);
		    break;
		case 5:
		    try {
			int alarmsDeletedCount = DatabaseOperations
				.executeUpdate("delete from recorded_alerts where patient_pid='" + patientId + "'");
			System.out.println("No. of alarms acknowledged: " + alarmsDeletedCount);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    break;
		case 6:
		    displayHealthSupporterMainMenu();
		    break;
		}
	    }
	} while (true);
    }

    /**
     * Displays health indicators for a patient.
     * <p>
     * <b>Note:</b> If health supporter is calling this function, only those
     * data whose recorded date is after start date of health supporter will be
     * displayed.
     * 
     * @param patientId
     *            PID of the patient whose health indicators are to be
     *            displayed.
     */
    private static void displayHealthIndicators(String patientId) {
	int i = 1;
	ResultSet rs = DatabaseOperations
		.executeQuery("SELECT * FROM health_observation WHERE id IN (SELECT DISTINCT health_obs_id "
			+ "FROM health_obs_frequency WHERE patient_pid = '" + Menus.pid
			+ "' UNION SELECT DISTINCT dr.health_obs_id FROM disease_recommendations dr, diagnosis d WHERE dr.disease_id = d.disease_id AND d.patient_pid = '"
			+ Menus.pid + "')");
	HashMap<Integer, Integer> healthIndicatorsMap = new HashMap<Integer, Integer>();
	try {
	    if (rs.next()) {
		System.out.println("\nChoose Your Health Indicators:\n");
		System.out.println(String.format("%3s %-30s %-60s %-12s %-12s %-12s", "No", "HealthObsName",
			"Description", "Data Type", "Lower Limit", "Upper Limit"));
		do {
		    int valID = rs.getInt("id");
		    String valName = rs.getString("name");
		    String valDescription = rs.getString("description");
		    String valDataType = rs.getString("data_type");
		    int valLowerLimit = rs.getInt("lower_limit");
		    int valUpperLimit = rs.getInt("upper_limit");
		    System.out.println(String.format("%3s %-30s %-60s %-12s %-12s %-12s", i, valName, valDescription,
			    valDataType, valLowerLimit, valUpperLimit));
		    healthIndicatorsMap.put(i++, valID);
		} while (rs.next());
	    } else {
		System.out.println("No health indicators to display.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	System.out.print("\nDo you want to add your health indicators (Y/N): ");
	String userInput = scanner.nextLine();
	if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
	    int userChoiceID = -1;
	    do {
		System.out.print("Enter the Health Observation no: ");
		userChoiceID = getUserChoice(scanner.nextLine(), healthIndicatorsMap.size());
	    } while (userChoiceID == -1);
	    System.out.print("Enter the recorded values: ");
	    String userEnteredValues = scanner.nextLine();
	    System.out.print("Enter the Observed Time [YYYY-MM-DD HH24:MI:SS]: ");
	    String userObservedTime = scanner.nextLine();
	    System.out.print("Enter the Recorded Time [YYYY-MM-DD HH24:MI:SS]: ");
	    String userRecordedTime = scanner.nextLine();
	    CallableStatement csForMoodMapping = null;
	    String call = "{? = call happy_mapping(?,?,?,?,?)}";
	    try {
		csForMoodMapping = DatabaseOperations.connection.prepareCall(call);
		csForMoodMapping.registerOutParameter(1, java.sql.Types.INTEGER);
		csForMoodMapping.setString(2, Menus.pid);
		csForMoodMapping.setInt(3, userChoiceID);
		csForMoodMapping.setString(4, userEnteredValues.toLowerCase());
		csForMoodMapping.setTimestamp(5, java.sql.Timestamp.valueOf(userObservedTime));
		csForMoodMapping.setTimestamp(6, java.sql.Timestamp.valueOf(userRecordedTime));
		csForMoodMapping.execute();
		if (csForMoodMapping.getInt(1) == 1) {
		    System.out.println("Data added successfully.");
		} else {
		    System.out.println("Returned Value: " + csForMoodMapping.getInt(1));
		    System.out.println("There was a problem adding data.");
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Displays main menu for the health supporter.
     */
    private static void displayHealthSupporterMainMenu() {
	int choice = 0;
	do {
	    System.out.println("Choose one of the following: ");
	    System.out.println("1. Profile");
	    System.out.println("2. View Patient Details");
	    System.out.println("3. Logout");
	    System.out.println("4. Exit");
	    System.out.print("Enter your choice (1-4): ");
	    choice = getUserChoice(scanner.nextLine(), 4);
	    switch (choice) {
	    case 1:
		displayProfileMenu(Menus.pid);
		break;
	    case 2:
		displayAllPatientDetailsForHS();
		break;
	    case 3:
		displayEntryMenu();
		break;
	    case 4:
		displayExitPage();
		break;
	    }
	} while (true);
    }

    /**
     * Displays all the recorded values for a patient.
     * <p>
     * <b>Note:</b> If health supporter is calling this function, only those
     * data whose recorded date is after start date of health supporter will be
     * displayed.
     * 
     * @param patientId
     *            PID of the patient whose recorded values are to be displayed.
     */
    private static void displayRecordedValues(String patientId) {
	try {
	    String queryForRsWithoutMood = "SELECT * FROM recorded_health_obs rho, health_observation"
		    + " WHERE health_observation.id=rho.health_obs_id AND patient_pid ='" + patientId
		    + "' AND rho.health_obs_id != 7";
	    String queryForRsMood = "SELECT ho.name, mm.mood_string AS recorded_value, rho.observed_time, rho.recorded_time"
		    + " FROM recorded_health_obs rho, health_observation ho, mood_mapping mm"
		    + " WHERE ho.id = rho.health_obs_id AND rho.patient_pid = '" + Menus.pid
		    + "' AND ho.id = 7 AND rho.recorded_value = mm.mood_number";
	    String suffixForHs = " AND rho.recorded_time>=(SELECT start_date from support" + " WHERE patient_pid='"
		    + patientId + "' AND health_supporter_pid='" + Menus.pid + "')";
	    if (!Menus.pid.equals(patientId)) {
		queryForRsWithoutMood += suffixForHs;
		queryForRsMood += suffixForHs;
	    }
	    ResultSet rsWithoutMood = DatabaseOperations.executeQuery(queryForRsWithoutMood);
	    ResultSet rsMood = DatabaseOperations.executeQuery(queryForRsMood);
	    boolean isTitleDisplayed = false;
	    if (rsWithoutMood.next()) {
		System.out.println(String.format("%-25s %-15s %-25s %-25s", "HealthObservationName", "RecordedValue",
			"ObservedTime", "RecordedTime"));
		isTitleDisplayed = true;
		do {
		    String valName = rsWithoutMood.getString("name");
		    int valRecordedValue = rsWithoutMood.getInt("recorded_value");
		    String valObservedTime = rsWithoutMood.getString("observed_time");
		    String valRecordedTime = rsWithoutMood.getString("recorded_time");
		    System.out.println(String.format("%-25s %-15s %-25s %-25s", valName, valRecordedValue,
			    valObservedTime, valRecordedTime));
		} while (rsWithoutMood.next());
	    }
	    if (rsMood.next()) {
		if (!isTitleDisplayed) {
		    System.out.println(String.format("%-25s %-15s %-25s %-25s", "HealthObservationName",
			    "RecordedValue", "ObservedTime", "RecordedTime"));
		}
		do {
		    String valName = rsMood.getString("name");
		    String valRecordedValue = rsMood.getString("recorded_value");
		    String valObservedTime = rsMood.getString("observed_time");
		    String valRecordedTime = rsMood.getString("recorded_time");
		    System.out.println(String.format("%-25s %-15s %-25s %-25s", valName, valRecordedValue,
			    valObservedTime, valRecordedTime));
		} while (rsMood.next());
	    } else if (!isTitleDisplayed) {
		System.out.println("\nNo recordings to display.\n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Checks whether the user enter choice is valid.
     * 
     * @param userChoice
     *            User entered value.
     * @param maxValue
     *            The maximum value allowed for given input.
     * @return Same value as userChoice if value is valid. -1 if userChoice is
     *         not between 1 to maxValue or is not integer.
     */
    private static int getUserChoice(String userChoice, int maxValue) {
	int option = -1;
	try {
	    option = Integer.parseInt(userChoice);
	    if (option < 1 || option > maxValue) {
		System.out.println("\nPlease enter number between 1 to " + maxValue + ".");
		return -1;
	    }
	} catch (Exception e) {
	    System.out.println("Wrong Input Format.");
	}
	return option;
    }
}
