package Database;

import java.io.*;
import java.util.logging.Logger;

import static Database.DBUtil.Constants.BLOCK_SIZE;
import static Database.DBUtil.Constants.DATABASE_DIRECTORY;

public class main {

    public static void main(String[] args) {
       // testCreate();
        //testPut();
        testDBController();
    }

    public static void testDBController() {
        // Initialize the DBController
        DBController dbController = new DBController();

        // Start the CLI with the simulated input
        dbController.startCLI();

    }
    public static void testPut() {
        System.out.println(System.getProperty("file.encoding"));
        String databaseName = "test_group1";
        String displayFileName = "test_output.txt";
        String tableName = "movies-test.csv";
        DBService dbService = new DBService(databaseName);
//        dbService.create();
        dbService.put(tableName);
        printTestFile("test_group1", displayFileName);
        System.out.println("Put completed");
    }

    // Create method works
    public static void testCreate() {
        System.out.println(System.getProperty("file.encoding"));
        String databaseName = "test_group1";
        String displayFileName = "test_output.txt";
        DBService dbService = new DBService(databaseName);
        dbService.create();
        //printPFSFile("test_group1.db0");
        printTestFile("test_group1", displayFileName);
        System.out.println("Create completed");
    }

    public static void printPFSFile(String fileName) {
        try {
            File file = new File(DATABASE_DIRECTORY + "/" + fileName);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
        System.out.println("Print completed");
    }

    public static void printTestFile(String inputTestFile, String outputTestFile) {
        try {
            File outputFile = new File(DATABASE_DIRECTORY + "/" + outputTestFile);
            if (outputFile.exists()) {
                outputFile.delete(); // Delete the existing output file
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_DIRECTORY + "/" + inputTestFile + ".db0"));
                 FileWriter writer = new FileWriter(outputFile)) {
                char[] buffer = new char[BLOCK_SIZE];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                    writer.write('\n'); // Write a newline character after each buffer
                }
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

    private static void printFileSystem(String databaseName, String fileName) {
        try {
            File file = new File(DATABASE_DIRECTORY + "/" + fileName);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }
}
