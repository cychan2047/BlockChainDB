package Database.DBUtil;

import Database.DBRepository;

import java.io.IOException;
import java.util.Queue;
import java.util.logging.Logger;

import static Database.DBUtil.Constants.*;

public class DataIndexFileRemover {

    private String databaseName;
    private String tableName;
    private DBRepository repo;

    private FCBReaderWriter FCBReaderWriter;

    public DataIndexFileRemover(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.repo = new DBRepository(databaseName);
        this.FCBReaderWriter = new FCBReaderWriter(databaseName);
    }

    public void removeData() {
        String rootIndexBlock = FCBReaderWriter.getRootIndexBlock(tableName);
        if (rootIndexBlock == null) {
            Logger.getLogger(DataIndexFileRemover.class.getName()).severe("Table not found");
            return;
        }
        int rootNum = Integer.parseInt(rootIndexBlock);
        // Run BFS to clear all data and index blocks
        try {
            Queue<Integer> queue = new java.util.LinkedList<>();
            queue.add(rootNum);
            int currentOffset = 0;
            while (!queue.isEmpty()) {
                int blockNum = queue.poll();
                int currentBlock = blockNum % BLOCK_NUM_PER_FILE;
                int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
                if (repo.readChar(PFSFileNum, FILE_TYPE_MARKER_OFFSET, currentBlock).equals(DATA_MARKER)) {
                    clearBlock(blockNum);
                    continue;
                }

                do {
                    int indexBlock = Integer.parseInt(repo.read(PFSFileNum, currentOffset, currentBlock, BLOCK_NUM_LENGTH));
                    if (indexBlock != 0 && indexBlock != 99999) {
                        queue.add(indexBlock);
                    } else if (indexBlock == 0) {
                        break;
                    }
                    int dataBlock = Integer.parseInt(repo.read(PFSFileNum, currentOffset + KEY_LENGTH + BLOCK_NUM_LENGTH, currentBlock, BLOCK_NUM_LENGTH));
                    if (dataBlock != 0 && dataBlock != 99999) {
                        queue.add(dataBlock);
                    }
                    currentOffset += BLOCK_NUM_LENGTH + KEY_LENGTH + BLOCK_NUM_LENGTH;
                } while (currentOffset < BLOCK_SIZE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearBlock(int blockNum) {
        try {
            int currentBlock = blockNum % Constants.BLOCK_NUM_PER_FILE;
            int PFSFileNum = blockNum / Constants.BLOCK_NUM_PER_FILE;
            for (int i = 0; i < Constants.NUM_OF_RECORDS; i++) {
                repo.write(PFSFileNum, 0, currentBlock, "0".repeat(RECORD_SIZE));
            }
            repo.write(PFSFileNum, Constants.FSM_PFS_FILE_NUM, currentBlock, "EOF  ");
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileRemover.class.getName()).severe(e.getMessage());
        }
    }

}
