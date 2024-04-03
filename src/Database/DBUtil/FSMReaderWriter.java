package Database.DBUtil;

import Database.DBRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

// Read and write the free space management block
public class FSMReaderWriter {

    private static final String INITIAL_BITMAP = "ffc" + "0".repeat(BLOCK_SIZE - 3);
    private static final String FOLLOWING_BITMAP = "004" + "0".repeat(BLOCK_SIZE - 3);

    private static final HashMap<String, String> hexToBin = new HashMap<>() {{
        put("0", "0000");
        put("1", "0001");
        put("2", "0010");
        put("3", "0011");
        put("4", "0100");
        put("5", "0101");
        put("6", "0110");
        put("7", "0111");
        put("8", "1000");
        put("9", "1001");
        put("a", "1010");
        put("b", "1011");
        put("c", "1100");
        put("d", "1101");
        put("e", "1110");
        put("f", "1111");
    }};

    private static final HashMap<String, String> binToHex = new HashMap<>() {{
        put("0000", "0");
        put("0001", "1");
        put("0010", "2");
        put("0011", "3");
        put("0100", "4");
        put("0101", "5");
        put("0110", "6");
        put("0111", "7");
        put("1000", "8");
        put("1001", "9");
        put("1010", "a");
        put("1011", "b");
        put("1100", "c");
        put("1101", "d");
        put("1110", "e");
        put("1111", "f");
    }};

    private String databaseName;
    private int currentIndex = 0;
    private Queue<Integer> freeBlocksBuffer;

    public FSMReaderWriter(String databaseName) {
        this.databaseName = databaseName;
        this.currentIndex = 0;
        this.freeBlocksBuffer = new LinkedList<>();
    }

    public void initialize(int PFSFileNum) {
        try {
            DBRepository repo = new DBRepository(databaseName);
            if (PFSFileNum == 0) {
                repo.write(PFSFileNum, FSM_BLOCK_OFFSET, FSM_BLOCK_NUM, INITIAL_BITMAP);
            } else {
                repo.write(PFSFileNum, FSM_BLOCK_OFFSET, FSM_BLOCK_NUM, FOLLOWING_BITMAP);
            }
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public boolean getAvailability(int blockNum) {
        // public String readChar(String databaseName, int PFSFileCount, int offset, int blockNum)
        try {
            DBRepository repo = new DBRepository(databaseName);
            String hex = repo.readChar(FSM_PFS_FILE_NUM, blockNum / 4, FSM_BLOCK_NUM);
            String binary = hexToBin.get(hex);
            return binary.charAt(blockNum % 4) == '0';
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
        return false;
    }

    public void setAvailability(int blockNum, boolean available) {
        // TODO: Handle the case when there are multiple PFSFiles
        int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
        int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
        try {
            DBRepository repo = new DBRepository(databaseName);
            String hex = repo.readChar(FSM_PFS_FILE_NUM, currentBlockNum / 4, FSM_BLOCK_NUM);
            String binary = hexToBin.get(hex);
            StringBuilder newBinary = new StringBuilder(binary);
            newBinary.setCharAt(blockNum % 4, available ? '0' : '1');
            String newHex = binToHex.get(newBinary.toString());
            repo.write(PFSFileNum, currentBlockNum / 4, FSM_BLOCK_NUM, newHex);
        } catch (IOException e) {
            Logger.getLogger(FSMReaderWriter.class.getName()).severe(e.getMessage());
        }
    }

    public int getNextAvailableBlock() {
        // Check the next available block.
        // If all full, create a new one and return the first block automatically.
        // BlockNum = PFSFileNum * BLOCK_NUM_PER_FILE + currentBlockNum
        DBRepository repo = new DBRepository(databaseName);
        File file;
        int currentPFSFileNum = METADATA_PFS_FILE_NUM;
        do {
            int result = getNextAvailableBlock(currentPFSFileNum);
            if (result != -1) {
                return result + currentPFSFileNum * BLOCK_NUM_PER_FILE;
            } else {
                currentPFSFileNum++;
            }
            file = new File(DATABASE_DIRECTORY + "/" + databaseName + ".db" + currentPFSFileNum);
        } while (file.exists());
        repo.createPFSFile(currentPFSFileNum);
        MetadataReaderWriter metadataReaderWriter = new MetadataReaderWriter(databaseName);
        int KVTableCount = metadataReaderWriter.getKVTableCount();
        metadataReaderWriter.write(currentPFSFileNum, KVTableCount);
        return currentPFSFileNum * BLOCK_NUM_PER_FILE;
    }


    public int getNextAvailableBlock(int PFSFileNum) {
        DBRepository repo = new DBRepository(databaseName);
        do {
            if (!freeBlocksBuffer.isEmpty()) {
                return freeBlocksBuffer.poll();
            }
            try {
                String hex = repo.readChar(PFSFileNum, currentIndex / 4, FSM_BLOCK_NUM);
                if (hex.equals("F")) {
                    currentIndex += 4;
                    continue;
                }
                System.out.println("hex: " + hex);
                System.out.println("currentIndex: " + currentIndex);
                String binary = hexToBin.get(hex);
                while (binary.length() < 4) {
                    binary = "0" + binary;
                }
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
