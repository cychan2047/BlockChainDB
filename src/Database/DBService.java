package Database;

import Database.DBUtil.FCBWriter;
import Database.DBUtil.MetadataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;


public class DBService {
    private int PFSFileCount;
    private DBRepository dbRepository;
    private static final String PFS_DIRECTORY = "./src/Database/PFSFiles";
    private static final String TABLE_DIRECTORY = "./src/KVTables";
    private static final int METADATA_PFS_FILE_NUM = 0;
    private String databaseName;

    private static final int BLOCK_SIZE = 256;

    private static final int FILE_SIZE = 1024 * 1024;



    public DBService(String databaseName) {
        this.PFSFileCount = 0;
        this.dbRepository = new DBRepository(BLOCK_SIZE, FILE_SIZE, PFS_DIRECTORY);
        this.databaseName = databaseName;
    }

    public void open() {};

    public void put(String TableName) {
        // TableName example: "movies-test.csv"
        DBRepository repo = new DBRepository(BLOCK_SIZE, FILE_SIZE, PFS_DIRECTORY);
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
        File directory = new File(dbRepository.getCurrentPath());
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".db"));

        if (files != null) {
            for (File file : files) {
                String databaseName = file.getName().split("\\.db")[0];

                StringBuilder metadata = new StringBuilder();
                StringBuilder fcbData = new StringBuilder();

                try {
                    // Read metadata from the METADATA_BLOCK_NUM
                    for (int i = 0; i < MetadataWriter.BLOCK_SIZE_OFFSET; i++) {
                        metadata.append(dbRepository.readChar(databaseName, METADATA_PFS_FILE_NUM, i, MetadataWriter.METADATA_BLOCK_NUM));
                    }

                    // Read file-specific data from the FCB_BLOCK_NUM
                    for (int i = 0; i < FCBWriter.STARTING_DATA_BLOCK_OFFSET; i++) {
                        fcbData.append(dbRepository.readChar(databaseName, METADATA_PFS_FILE_NUM, i, FCBWriter.FCB_BLOCK_NUM));
                    }

                    System.out.println("Metadata: " + metadata.toString());
                    System.out.println("FCB Data: " + fcbData.toString());
                } catch (IOException e) {
                    Logger.getLogger(DBService.class.getName()).severe("Error reading file data: " + e.getMessage());
                }
            }
        } else {
            System.out.println("No PFS files found in the database directory.");
        }
    }



    public void find(String tableName, int key) {};

    public void putr(String pathname, String remark) {};

    public void kill(String databaseName) {
        File directory = new File(dbRepository.getCurrentPath());
        File[] files = directory.listFiles((dir, name) -> name.startsWith(databaseName + ".db"));

        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + file.getName());
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        } else {
            System.out.println("No PFS files found for the database: " + databaseName);
        }
    }

    public void quit() {};

}
