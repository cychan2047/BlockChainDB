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
    private final String databaseName;

    private static final int BLOCK_SIZE = 256;

    private static final int FILE_SIZE = 1024 * 1024;



    public DBService(String databaseName) {
        this.PFSFileCount = 0;
        this.dbRepository = new DBRepository(BLOCK_SIZE, FILE_SIZE, PFS_DIRECTORY);
        this.databaseName = databaseName;
    }

    public DBService(int PFSFileCount, String databaseName) {
        this.PFSFileCount = PFSFileCount;
        this.dbRepository = new DBRepository(BLOCK_SIZE, FILE_SIZE, PFS_DIRECTORY);
        this.databaseName = databaseName;
    }

    public void open() {};

    public void put(String databaseName, int METADATA_PFS_FILE_NUM, String TableName) {
        // TableName example: "movies-test.csv"
        DBRepository repo = new DBRepository(BLOCK_SIZE, FILE_SIZE, PFS_DIRECTORY);
        File file = new File(TABLE_DIRECTORY + "/" + TableName);
        int PFSFileNum = 0;
        int blockNum;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {

            }
        } catch (IOException e) {
            Logger.getLogger(DBService.class.getName()).severe(e.getMessage());
        }
    };

    public void get() {};

    public void rm() {};

    public void dir() {};

    public void find(int key) {};

    public void putr(String pathname, String remark) {};

    public void kill() {};

    public void quit() {};

}
