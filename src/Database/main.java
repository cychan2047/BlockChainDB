package Database;

import java.io.*;
import java.util.logging.Logger;

import static Database.DBUtil.Constants.*;

public class main {

    public static void main(String[] args) {
//        System.out.println("Hello World");
//        testCreate();
//        testPut();
//        testGet();
//        testRm();
//        testFind();
//        testDir();
//        testKill();
        testDBController();
        //testPutLarge();
    }

    public static void testPutLarge() {
        String databaseName = "test_group1";
        String displayFileName = "test_output.txt";
        String tableName = "movies-small.csv";
        DBService dbService = new DBService(databaseName);
        dbService.put(tableName);
        printTestFile("test_group1", displayFileName, 0);
        System.out.println("Put completed");
    }

    public static void testDBController() {
        // Initialize the DBController
        DBController dbController = new DBController();

        // Start the CLI with the simulated input
        dbController.startCLI();

    }

    public static void testCreate() {
        String databaseName = "test_group1";
        DBRepository repo = new DBRepository(databaseName);
        repo.createPFSFile(0);
        printTestFile("test_group1", "test_output.txt",0);
//        repo.createPFSFile(1);
//        printTestFile("test_group1", "test_output2.txt", 1);
        System.out.println("Create completed");
    }

    public static void testKill() {
        String databaseName = "test_group1";
        DBService dbService = new DBService(databaseName);
        dbService.kill(databaseName);
        System.out.println("Kill completed");
    }

    public static void testDir() {
        String databaseName = "test_group1";
        DBService dbService = new DBService(databaseName);
        dbService.dir();
        System.out.println("Dir completed");
    }


    public static void testFind() {
        String databaseName = "test_group1";
        String tableName = "movies-test.csv";
        int key = 100;
        DBService dbService = new DBService(databaseName);
        dbService.find(tableName, key);
        System.out.println("Find completed");
    }

    public static void testRm() {
        String databaseName = "test_group1";
        String tableName = "movies-test.csv";
        DBService dbService = new DBService(databaseName);
        dbService.rm(tableName);
        printTestFile("test_group1", "test_output.txt", 0);
        System.out.println("Remove completed");
    }

    public static void testGet() {
        String databaseName = "test_group1";
        String tableName = "movies-test.csv";
        String OSPath = "./src/Database/PFSFiles";
        DBService dbService = new DBService(databaseName);
        dbService.get(OSPath, tableName);
        System.out.println("Get completed");
    }

    public static void testPut() {
        System.out.println("Put started");
        String databaseName = "test_group1";
        String displayFileName = "test_output.txt";
        String tableName = "movies-test.csv";
        DBService dbService = new DBService(databaseName);
////        dbService.create();
        dbService.put(tableName);
        printTestFile("test_group1", displayFileName, 0);
        System.out.println("Put completed");
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

    public static void printTestFile(String inputTestFile, String outputTestFile, int PFSFileCount) {
        try {
            File outputFile = new File(DATABASE_DIRECTORY + "/" + outputTestFile);
            if (outputFile.exists()) {
                outputFile.delete(); // Delete the existing output file
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_DIRECTORY + "/" + inputTestFile + ".db" + PFSFileCount));
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
