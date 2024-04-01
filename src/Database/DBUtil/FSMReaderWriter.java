package Database.DBUtil;

import Database.DBRepository;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

// Read and write the free space management block
public class FSMReaderWriter {

    private static final String INITIAL_BITMAP = "0".repeat(BLOCK_SIZE);

    private String databaseName;
    private int currentIndex = 0;
    private Queue<Integer> freeBlocksBuffer;

    public FSMReaderWriter(String databaseName) {
        this.databaseName = databaseName;
        this.currentIndex = 0;
        this.freeBlocksBuffer = new LinkedList<>();
    }

    public void initialize() {
        try {
            DBRepository repo = new DBRepository(databaseName);
            repo.write(FSM_BLOCK_NUM, FSM_BLOCK_OFFSET, FSM_BLOCK_NUM, INITIAL_BITMAP);
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public boolean getAvailability(int blockNum) {
        // public String readChar(String databaseName, int PFSFileCount, int offset, int blockNum)
        try {
            DBRepository repo = new DBRepository(databaseName);
            String hex = repo.readChar(FSM_PFS_FILE_NUM, blockNum / 4, FSM_BLOCK_NUM);
            int decimal = Integer.parseInt(hex, 16);
            String binary = Integer.toBinaryString(decimal);
            return binary.charAt(blockNum % 4) == '0';
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
        return false;
    }

    public void setAvailability(int blockNum, boolean available) {
        // TODO: Handle the case when there are multiple PFSFiles
        try {
            DBRepository repo = new DBRepository(databaseName);
            String hex = repo.readChar(FSM_PFS_FILE_NUM, blockNum / 4, FSM_BLOCK_NUM);
            int decimal = Integer.parseInt(hex, 16);
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(decimal));
            binary.setCharAt(blockNum % 4, available ? '0' : '1');
            int newDecimal = Integer.parseInt(binary.toString(), 2);
            String newHex = Integer.toHexString(newDecimal);
            repo.write(FSM_BLOCK_NUM, blockNum / 4, FSM_BLOCK_NUM, newHex);
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public int getNextAvailableBlock() {
        DBRepository repo = new DBRepository(databaseName);
        do {
            if (!freeBlocksBuffer.isEmpty()) {
                return freeBlocksBuffer.poll();
            }
            try {
                String hex = repo.readChar(FSM_PFS_FILE_NUM, currentIndex / 4, FSM_BLOCK_NUM);
                if (hex.equals("F")) {
                    currentIndex += 4;
                    continue;
                }
                int decimal = Integer.parseInt(hex, 16);
                String binary = Integer.toBinaryString(decimal);
                for (int i = currentIndex % 4; i < 4; i++) {
                    if (binary.charAt(i) == '0') {
                        int blockNum = currentIndex + i;
                        setAvailability(blockNum, false);
                        return blockNum;
                    }
                }
                currentIndex += 4;
            } catch (IOException e) {
                Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
            }
        } while (currentIndex < FILE_SIZE / BLOCK_SIZE);
        return -1;
    }
}
