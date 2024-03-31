package Database.DBUtil;

import Database.DBRepository;

import java.io.*;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class MetadataWriter {

    public MetadataWriter() {}

    public void write(String databaseName, int PFSFileNum, int blockSize, int kvTableCount) {
        DBRepository repo = new DBRepository();
        try {
            repo.write(databaseName, METADATA_PFS_FILE_NUM, DB_NAME_OFFSET, METADATA_BLOCK_NUM, databaseName);
            repo.write(databaseName, METADATA_PFS_FILE_NUM, DB_SIZE_OFFSET, METADATA_BLOCK_NUM, Integer.toString(FILE_SIZE));
            repo.write(databaseName, METADATA_PFS_FILE_NUM, PFS_FILE_COUNT_OFFSET, METADATA_BLOCK_NUM, Integer.toString(PFSFileNum));
            repo.write(databaseName, METADATA_PFS_FILE_NUM, BLOCK_SIZE_OFFSET, METADATA_BLOCK_NUM, Integer.toString(blockSize));
            repo.write(databaseName, METADATA_PFS_FILE_NUM, KV_TABLE_COUNT, METADATA_BLOCK_NUM, Integer.toString(kvTableCount));
        } catch (IOException e) {
            Logger.getLogger(MetadataWriter.class.getName()).severe(e.getMessage());
        }
    }
}
