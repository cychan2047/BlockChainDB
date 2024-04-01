package Database.DBUtil;

import Database.DBRepository;
import java.io.IOException;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

// Read and write the free space management block
public class FSMReaderWriter {

    private static final String INITIAL_BITMAP = "0".repeat(BLOCK_SIZE);

    private String databaseName;
    public FSMReaderWriter(String databaseName) {
        this.databaseName = databaseName;
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

    public int getFirstAvailableBlock() {
        DBRepository repo = new DBRepository(databaseName);
        for (int i = 0; i < BLOCK_SIZE; i++) {
            try {
                String hex = repo.readChar(FSM_PFS_FILE_NUM, i, FSM_BLOCK_NUM);
                if (hex.equals("F")) continue;
                int decimal = Integer.parseInt(hex, 16);
                String binary = Integer.toBinaryString(decimal);
                for (int j = 0; j < 4; j++) {
                    if (binary.charAt(j) == '0') {
                        return i * 4 + j;
                    }
                }
            } catch (IOException e) {
                Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
            }
        }
        return -1;
    }
}
