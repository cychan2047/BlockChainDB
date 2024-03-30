package Database.DBUtil;

import Database.DBRepository;

import java.io.*;
import java.util.logging.Logger;

public class MetadataWriter {
    private static final int BLOCK_NUM = 0;
    private static final int DB_NAME_OFFSET = 0;
    private static final int DB_SIZE_OFFSET = 50;
    private static final int PFS_FILE_COUNT_OFFSET = 60;
    private static final int BLOCK_SIZE_OFFSET = 70;
    private static final int KV_TABLE_COUNT = 80;
    private static final int METADATA_PFS_FILE_NUM = 0;
    private static final int METADATA_BLOCK_NUM = 0;
    private final int BLOCK_SIZE;
    private final int FILE_SIZE;
    private final String dataFileDirectory;

    public MetadataWriter(int blockSize, int fileSize, String dataFileDirectory) {
        this.BLOCK_SIZE = blockSize;
        this.FILE_SIZE = fileSize;
        this.dataFileDirectory = dataFileDirectory;
    }

    public void write(String databaseName, int PFSFileNum, int blockSize, int kvTableCount) throws IOException {
        DBRepository repo = new DBRepository(BLOCK_SIZE, FILE_SIZE, dataFileDirectory);
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
