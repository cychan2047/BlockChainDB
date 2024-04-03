package Database.DBUtil;

import Database.DBRepository;

import java.io.IOException;
import java.util.logging.Logger;

import static Database.DBUtil.Constants.*;

public class DataIndexFileReader {

    private String databaseName;

    private String tableName;

    private FCBReaderWriter FCBReaderWriter;

    private DBRepository repo;

    public DataIndexFileReader(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.repo = new DBRepository(databaseName);
        this.FCBReaderWriter = new FCBReaderWriter(databaseName);
    }

    public String find(int key) {
        int[] result = findDataBlockByKey(key);
        StringBuilder sb = new StringBuilder();
        if (result == null) {
            return null;
        }
        int blockNum = result[0];
        int accessedBlockCount = result[1];
        try {
            int currentBlock = blockNum % BLOCK_NUM_PER_FILE;
            int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
            int currentOffset = 0;
            do {
                String record = repo.read(PFSFileNum, currentOffset, currentBlock, RECORD_SIZE);
                if (record.split(",")[0].equals(Integer.toString(key))) {
                    return sb.append(record).append("\n")
                            .append("# of blocks = ").append(accessedBlockCount).append("\n")
                            .toString();
                }
                currentOffset += RECORD_SIZE;
            } while (currentOffset < BLOCK_SIZE);
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileReader.class.getName()).severe(e.getMessage());
        }
        return null;
    }

    public int[] findDataBlockByKey(int key) {
        int accessedBlockCount = 0;
        String rootIndexBlock = FCBReaderWriter.getRootIndexBlock(tableName).trim();
        if (rootIndexBlock == null) {
            Logger.getLogger(DataIndexFileReader.class.getName()).severe("Table not found");
            return null;
        }
        int currentBlockNum = Integer.parseInt(rootIndexBlock);
        try {
            int currentOffset = BLOCK_NUM_LENGTH;
            do {
                int currentBlock = currentBlockNum % BLOCK_NUM_PER_FILE;
                int PFSFileNum = currentBlockNum / BLOCK_NUM_PER_FILE;
                accessedBlockCount++;
                do {
                    // Increment the accessed block count
                    int currentKey = Integer.parseInt(repo.read(PFSFileNum, currentOffset, currentBlock, KEY_LENGTH));
                    if (currentKey == key) {
                        int resultBlockNum = Integer.parseInt(repo.read(PFSFileNum, currentOffset + KEY_LENGTH, currentBlock, BLOCK_NUM_LENGTH));
                        return new int[] {resultBlockNum, accessedBlockCount};
                    } else if (currentKey > key) {
                        // Go to the left child
                        currentBlockNum = Integer.parseInt(repo.read(PFSFileNum, currentOffset - BLOCK_NUM_LENGTH, currentBlock, BLOCK_NUM_LENGTH));
                        break;
                    }
                    currentOffset += KEY_LENGTH + BLOCK_NUM_LENGTH;
                } while (currentOffset < BLOCK_SIZE);
            } while (currentBlockNum != 0);
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileReader.class.getName()).severe(e.getMessage());
        }
        return null;
    }

}
