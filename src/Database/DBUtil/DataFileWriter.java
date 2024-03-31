package Database.DBUtil;

import java.io.File;

import Database.DBRepository;
import static Database.DBUtil.Constants.*;

public class DataFileWriter {

    private DBRepository repo;
    private File tableFile;

    private FSMReaderWriter fsmReaderWriter;

    public DataFileWriter(String tableName, String databaseName) {
        this.tableFile = new File(TABLE_DIRECTORY + "/" + tableName);
        this.repo = new DBRepository(databaseName);
        this.fsmReaderWriter = new FSMReaderWriter(databaseName);
    }

    // Read the data from the csv tableFile and write it into the PFSFiles
    public void write() {

    }




}
