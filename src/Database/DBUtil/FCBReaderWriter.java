package Database.DBUtil;

import Database.DBRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class FCBReaderWriter {

    private String databaseName;

    private DBRepository repo;

    public FCBReaderWriter(String databaseName) {
        this.databaseName = databaseName;
        this.repo = new DBRepository(databaseName);
    }

    public void write(String tableName, String tableSize, String dateTime,
                      String dataStartingBlockNum, String indexRootBlockNum,
                      String dataEndingBlockNum, int fcbNum) {
        DBRepository repo = new DBRepository(databaseName);
        try {
            repo.write(FCB_PFS_FILE_NUM, TABLE_NAME_OFFSET, fcbNum, tableName);
            repo.write(FCB_PFS_FILE_NUM, TABLE_SIZE_OFFSET, fcbNum, tableSize);
            repo.write(FCB_PFS_FILE_NUM, TABLE_TIME_OFFSET, fcbNum, dateTime);
            repo.write(FCB_PFS_FILE_NUM, STARTING_DATA_BLOCK_OFFSET, fcbNum, dataStartingBlockNum);
            repo.write(FCB_PFS_FILE_NUM, ROOT_INDEX_BLOCK_OFFSET, fcbNum, indexRootBlockNum);
            repo.write(FCB_PFS_FILE_NUM, ENDING_DATA_BLOCK_OFFSET, fcbNum, dataEndingBlockNum);
            repo.write(FCB_PFS_FILE_NUM, FCB_AVAILABILITY_OFFSET, fcbNum, FCB_NOT_AVAILABLE_MARKER);
        } catch (IOException e) {
            Logger.getLogger(FCBReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public void initialize() {
        try {
            for (int i = STARTING_FCB_NUM; i <= ENDING_FCB_NUM; i++) {
                repo.write(FCB_PFS_FILE_NUM, FILE_TYPE_MARKER_OFFSET, i, FCB_MARKER);
                repo.write(FCB_PFS_FILE_NUM, FCB_AVAILABILITY_OFFSET, i, FCB_AVAILABLE_MARKER);
            }
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
                    return repo.read(FSM_PFS_FILE_NUM, ROOT_INDEX_BLOCK_OFFSET, currentIndex, ENDING_DATA_BLOCK_OFFSET - ROOT_INDEX_BLOCK_OFFSET);
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

    public int getNextAvailableFCB() throws IllegalStateException {
        try {
            for (int i = STARTING_FCB_NUM; i < ENDING_FCB_NUM; i++) {
                System.out.println("Trying: " + i);
                System.out.println(repo.readBlock(FCB_PFS_FILE_NUM, i));
                if (repo.readChar(FCB_PFS_FILE_NUM, FILE_TYPE_MARKER_OFFSET, i).equals(FCB_MARKER) &&
                        repo.readChar(FCB_PFS_FILE_NUM, FCB_AVAILABILITY_OFFSET, i).equals(FCB_AVAILABLE_MARKER)) {
                    return i;
                }
            }
        } catch (IOException e) {
            Logger.getLogger(FCBReaderWriter.class.getName()).severe(e.getMessage());
        }
        throw new IllegalStateException("No available FCB");
    }

    public String getCurrentDataTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
