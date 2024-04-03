package Database;

import Database.DBUtil.DataIndexFileWriter;
import Database.DBUtil.FSMReaderWriter;
import Database.DBUtil.MetadataWriter;

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

    public void create() {
        // Creates a new database file
        dbRepository.createPFSFile(PFSFileCount);
        MetadataWriter metadataWriter = new MetadataWriter(databaseName);
        metadataWriter.write(1, 0);
        FSMReaderWriter fsmReaderWriter = new FSMReaderWriter(databaseName);
        fsmReaderWriter.initialize(0);
    }

    public void put(String tableName) {
        // TableName example: "movies-test.csv"
        DataIndexFileWriter writer = new DataIndexFileWriter(tableName, databaseName);
        writer.writeDataFile(tableName);
    };

    public void get(String OSPath, String tableName) {};

    public void rm(String tableName) {};

    public void dir() {
        // Lists all the metadata and FCB info
        File directory = new File(DATABASE_DIRECTORY);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".db0"));

        if (files != null) {
            for (File file : files) {
                String databaseName = file.getName().split("\\.db")[0];
                System.out.println("Database: " + databaseName);

                StringBuilder metadata = new StringBuilder();
                StringBuilder fcbData = new StringBuilder();

                try {
                    // Read metadata from the METADATA_BLOCK_NUM
                    for (int i = 0; i < BLOCK_SIZE_OFFSET; i++) {
                        metadata.append(dbRepository.readChar(METADATA_PFS_FILE_NUM, i, METADATA_BLOCK_NUM));

                        String metadataName = metadata.length() >= 50 ? metadata.substring(0, 50).trim() : metadata.toString();
                        String metadataSize = metadata.length() >= 60 ? metadata.substring(50, 60).trim() : "N/A";
                        String metadataCount = metadata.length() >= 70 ? metadata.substring(60, 70).trim() : "N/A";

                        System.out.println("MetaData: " + metadataName + "Size: " + metadataSize + "FileCount: " + metadataCount);
                    }

                    // Read file-specific data from the FCB_BLOCK_NUM
                    for (int i = FCB_BLOCK_NUM; i < FSM_BLOCK_NUM; i++) {
                        if (dbRepository.readChar(METADATA_PFS_FILE_NUM, 0, i).equals(" ")) {
                            break;
                        } else {
                            for (int j = 0; j < STARTING_DATA_BLOCK_OFFSET; j++) {
                                fcbData.append(dbRepository.readChar(METADATA_PFS_FILE_NUM, j, i));

                                String fcbDataName = fcbData.length() >= 50 ? fcbData.substring(0, 50).trim() : fcbData.toString();
                                String fcbDataSize = fcbData.length() >= 60 ? fcbData.substring(50, 60).trim() : "N/A";
                                String fcbDataTime = fcbData.length() >= 70 ? fcbData.substring(60, 70).trim() : "N/A";

                                System.out.println("FCB Data: " + fcbDataName + "Size: " + fcbDataSize + "Time: " + fcbDataTime);
                            }
                            fcbData.append("\n");
                        }

                    }

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

    public void quit() {
        System.out.println("Closing the database and exiting.");
    }
}
