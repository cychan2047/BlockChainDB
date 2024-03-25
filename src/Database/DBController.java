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

    public void startCLI() {
        System.out.println("NoSQL>");
        String command;
        while(!(command = scanner.nextLine().trim().equalsIgnoreCase(("quit")){
            processCommand(command.trim());
            System.out.println("NoSQL>");
        }
        System.out.println("Exiting NoSQL...");
    }

    private void processCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        if (parts.length == 0) {
            return;
        }
        String action = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        switch (action) {
            case "open":
                dbService.open();
                break;
            case "put":
                dbService.put(argument);
                break;
            case "get":
                dbService.get(argument);
                break;
            case "rm":
                dbService.rm(argument);
                break;
            case "dir":
                dbService.dir();
                break;
            case "find":
                dbService.find(argument);
                break;
            case "kill":
                dbService.kill(argument);
                break;
            case "quit":
                dbService.quit();
                break;
            default:
                System.out.println("Invalid command: " + parts[0]);
        }
    }

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