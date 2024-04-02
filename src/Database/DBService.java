package Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;


public class DBService {
    private int PFSFileCount;
    private DBRepository dbRepository;
    private String databaseName;

    // Constructor: Initializes the DBService with a specific database name
    public DBService(String databaseName) {
        this.PFSFileCount = 0;
        this.dbRepository = new DBRepository(databaseName);
        this.databaseName = databaseName;
    }


    public void put(String TableName) {
        // TableName example: "movies-test.csv"
        DBRepository repo = new DBRepository(databaseName);
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
        // Lists all the metadata and FCB info
        File directory = new File(DATABASE_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".db"));

        if (files != null) {
            for (File file : files) {
                String databaseName = file.getName().split("\\.db")[0];

                StringBuilder metadata = new StringBuilder();
                StringBuilder fcbData = new StringBuilder();

                try {
                    // Read metadata from the METADATA_BLOCK_NUM
                    for (int i = 0; i < BLOCK_SIZE_OFFSET; i++) {
                        metadata.append(dbRepository.readChar(METADATA_PFS_FILE_NUM, i, METADATA_BLOCK_NUM));
                    }

                    // Read file-specific data from the FCB_BLOCK_NUM
                    for (int i = FCB_BLOCK_NUM; i < FSM_BLOCK_NUM; i++) {
                        if (dbRepository.readChar(METADATA_PFS_FILE_NUM, i, FCB_BLOCK_NUM).equals("")) {
                            break;
                        } else {
                            for (int j = 0; j < STARTING_DATA_BLOCK_OFFSET; j++) {
                                fcbData.append(dbRepository.readChar(METADATA_PFS_FILE_NUM, j, i));
                            }
                            fcbData.append("\n");
                        }

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

    public void kill(String databaseName) {
        // Deletes all files related to a specific database
        File directory = new File(DATABASE_DIRECTORY);
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
