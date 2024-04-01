package Database.DBUtil;

import Database.DBRepository;

import java.io.*;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class FCBReaderWriter {

    private String databaseName;

    private DBRepository repo;

    public FCBReaderWriter(String databaseName) {
        this.databaseName = databaseName;
        this.repo = new DBRepository(databaseName);
    }

    public void write(int FCBBlockNum, String tableName, int tableSize, String tableTime, int startingDataBlock, int rootIndexBlock, int usedDataBlockCount, String remark) throws IOException {
        DBRepository repo = new DBRepository(databaseName);
        if (remark.length() > 40) {
            remark = remark.substring(0, 40);
        }
        try {
            repo.write(FCBBlockNum, TABLE_NAME_OFFSET, FCB_BLOCK_NUM, tableName);
            repo.write(FCBBlockNum, TABLE_SIZE_OFFSET, FCB_BLOCK_NUM, Integer.toString(tableSize));
            repo.write(FCBBlockNum, TABLE_TIME_OFFSET, FCB_BLOCK_NUM, tableTime);
            repo.write(FCBBlockNum, STARTING_DATA_BLOCK_OFFSET, FCB_BLOCK_NUM, Integer.toString(startingDataBlock));
            repo.write(FCBBlockNum, ROOT_INDEX_BLOCK_OFFSET, FCB_BLOCK_NUM, Integer.toString(rootIndexBlock));
            repo.write(FCBBlockNum, USED_DATA_BLOCK_COUNT, FCB_BLOCK_NUM, Integer.toString(usedDataBlockCount));
            repo.write(FCBBlockNum, REMARK_OFFSET, FCB_BLOCK_NUM, remark);
        } catch (IOException e) {
            Logger.getLogger(FCBReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public String getRootIndexBlock(String tableName) {
        try {
            int currentIndex = FCB_BLOCK_NUM;
            while (repo.readChar(FSM_PFS_FILE_NUM, FILE_TYPE_MARKER_OFFSET, currentIndex).equals(FCB_MARKER)) {
                String currentTableName = repo.read(FSM_PFS_FILE_NUM, TABLE_NAME_OFFSET, currentIndex, TABLE_SIZE_OFFSET - TABLE_NAME_OFFSET);
                if (removeTrailingSpaces(currentTableName).equals(tableName)) {
                    return repo.read(FSM_PFS_FILE_NUM, ROOT_INDEX_BLOCK_OFFSET, currentIndex, USED_DATA_BLOCK_COUNT - ROOT_INDEX_BLOCK_OFFSET);
                }
                currentIndex++;
            }
        } catch (IOException e) {
            Logger.getLogger(DataFileReader.class.getName()).severe(e.getMessage());
        }
        return null;
    }

    public String getStartingDataBlock(String tableName) {
        try {
            int currentIndex = FCB_BLOCK_NUM;
            while (repo.readChar(FSM_PFS_FILE_NUM, FILE_TYPE_MARKER_OFFSET, currentIndex).equals(FCB_MARKER)) {
                String currentTableName = repo.read(FSM_PFS_FILE_NUM, TABLE_NAME_OFFSET, currentIndex, TABLE_SIZE_OFFSET - TABLE_NAME_OFFSET);
                if (removeTrailingSpaces(currentTableName).equals(tableName)) {
                    return repo.read(FSM_PFS_FILE_NUM, STARTING_DATA_BLOCK_OFFSET, currentIndex, ROOT_INDEX_BLOCK_OFFSET - STARTING_DATA_BLOCK_OFFSET);
                }
                currentIndex++;
            }
        } catch (IOException e) {
            Logger.getLogger(DataFileReader.class.getName()).severe(e.getMessage());
        }
        return null;
    }

    public String removeTrailingSpaces (String s){
        int endIndex = s.length() - 1;
        while (endIndex >= 0 && Character.isWhitespace(s.charAt(endIndex))) {
            endIndex--;
        }
        return s.substring(0, endIndex + 1);
    }

}
