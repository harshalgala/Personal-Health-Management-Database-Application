package healthapp;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
	boolean retryConnection = true;
	Scanner scanner = new Scanner(System.in);
	do {
	    System.out.println("\nConnecting to database. Please wait.\n");
	    if (DatabaseOperations.isConnectionSuccessful()) {
		Menus.displayEntryMenu();
	    } else {
		System.out.println("Do you want to try again?");
		System.out.print("Press N to exit, anything else to retry: ");
		String userInput = scanner.nextLine();
		if (userInput.equalsIgnoreCase("n") || userInput.equalsIgnoreCase("no")) {
		    retryConnection = false;
		    System.out.println("\nExiting the application due to connection failure.\n");
		}
	    }
	} while (retryConnection);
	scanner.close();
    }
}
