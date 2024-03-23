package Database;

import java.util.Scanner;

public class DBController {

    private DBService dbService;

    private Scanner scanner;

    private String userInput;

    public DBController() {
        this.dbService = new DBService(); // TODO: Write function to decide which constructor to use
        this.scanner = new Scanner(System.in);
    }

    // TODO: Implement the following methods

    public DBService getDbService() {
        return dbService;
    }

    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}