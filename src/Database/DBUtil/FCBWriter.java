package Database.DBUtil;

import Database.DBRepository;

import java.io.*;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class FCBWriter {
    private final String dataFileDirectory;

    public FCBWriter(int blockSize, int fileSize, String dataFileDirectory) {
        this.dataFileDirectory = dataFileDirectory;
    }

    public void write(String databaseName, int FCBBlockNum, String tableName, int tableSize, String tableTime, int startingDataBlock, int rootIndexBlock, int usedDataBlockCount, String remark) throws IOException {
        DBRepository repo = new DBRepository();
        if (remark.length() > 40) {
            remark = remark.substring(0, 40);
        }
        try {
            repo.write(databaseName, FCBBlockNum, TABLE_NAME_OFFSET, FCB_BLOCK_NUM, tableName);
            repo.write(databaseName, FCBBlockNum, TABLE_SIZE_OFFSET, FCB_BLOCK_NUM, Integer.toString(tableSize));
            repo.write(databaseName, FCBBlockNum, TABLE_TIME_OFFSET, FCB_BLOCK_NUM, tableTime);
            repo.write(databaseName, FCBBlockNum, STARTING_DATA_BLOCK_OFFSET, FCB_BLOCK_NUM, Integer.toString(startingDataBlock));
            repo.write(databaseName, FCBBlockNum, ROOT_INDEX_BLOCK_OFFSET, FCB_BLOCK_NUM, Integer.toString(rootIndexBlock));
            repo.write(databaseName, FCBBlockNum, USED_DATA_BLOCK_COUNT, FCB_BLOCK_NUM, Integer.toString(usedDataBlockCount));
            repo.write(databaseName, FCBBlockNum, REMARK_OFFSET, FCB_BLOCK_NUM, remark);

        } catch (IOException e) {
            Logger.getLogger(FCBWriter.class.getName()).severe(e.getMessage());
        }
    }

}
