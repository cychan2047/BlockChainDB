package Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class DBService {
    private int PFSFileCount;
    private DBRepository dbRepository;
    private static final String PFS_DIRECTORY = "./src/Database/PFSFiles";
    private static final String TABLE_DIRECTORY = "./src/Database/KVTables";
    private static final int METADATA_PFS_FILE_NUM = 0;
    private String databaseName;

    private static final int BLOCK_SIZE = 256;

    private static final int FILE_SIZE = 1024 * 1024;



    public DBService(String databaseName) {
        this.PFSFileCount = 0;
        this.dbRepository = new DBRepository();
        this.databaseName = databaseName;
    }

    public void put(String TableName) {
        // TableName example: "movies-test.csv"
        DBRepository repo = new DBRepository();
        File file = new File(TABLE_DIRECTORY + "/" + TableName);
        int PFSFileNum = 0;
        int blockNum;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //TODO: Implement the logic to write the data to the database
            }
        } catch (IOException e) {
            Logger.getLogger(DBService.class.getName()).severe(e.getMessage());
        }
    };

    public void get(String OSPath, String tableName) {};

    public void rm(String tableName) {};

    public void dir() {
        File directory = new File(TABLE_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No files found in the database directory.");
        }
    };

    public void find(String tableName, int key) {};

    public void putr(String pathname, String remark) {};

    public void kill(String databaseName) {};

    public void quit() {};

}
