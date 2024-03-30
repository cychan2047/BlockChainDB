package Database;

import java.util.Scanner;

public class DBController {

    private DBService dbService;

    private Scanner scanner;

    private String userInput;

    // Manages command-line interaction for the database
    public DBController() {
        this.scanner = new Scanner(System.in);
    }

    // Starts the command-line interface
    public void startCLI() {
        String command;
        do {
            System.out.println("NoSQL>");
            command = scanner.nextLine().trim();
            processCommand(command);
        } while (!command.equalsIgnoreCase("quit"));
        System.out.println("Exiting NoSQL...");
    }

    // Processes a command entered by the user
    private void processCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        if (parts.length == 0) {
            return;
        }
        String action = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        if (action.equals("open")) {
            dbService = new DBService(argument);
        } else if (dbService == null) {
            System.out.println("No database is currently open. Please open a database first.");
        } else {
            switch (action) {
                case "put":
                    dbService.put(argument);
                    break;
                case "get":
                    dbService.get(System.getProperty("user.dir"), argument);
                    break;
                case "rm":
                    dbService.rm(argument);
                    break;
                case "dir":
                    dbService.dir();
                    break;
                case "find":
                    int lastDotIndex = argument.lastIndexOf(".");
                    String tableName = argument.substring(0, lastDotIndex);
                    int key = Integer.parseInt(argument.substring(lastDotIndex + 1));
                    dbService.find(tableName, key);
                    break;
                case "putr":
                    String[] partsArgument = argument.split("\\s+", 2);
                    if (partsArgument.length != 2) {
                        System.out.println("Invalid command: putr requires two arguments");
                        break;
                    }
                    dbService.putr(partsArgument[0], partsArgument[1]);
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


    }

    // Getters and setters
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