package Database.DBUtil;

import java.io.File;

import Database.DBRepository;
import static Database.DBUtil.Constants.*;

public class DataFileWriter {

    private DBRepository repo;
    private File tableFile;

    public DataFileWriter(File tableFile) {
        this.repo = new DBRepository();
        this.tableFile = tableFile;
    }

    // Read the data from the csv tableFile and write it into the




}
