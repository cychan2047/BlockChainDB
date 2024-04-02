package Database.DBUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import BTree.BTree;
import BTree.BTreeUtil.BTreeNode;
import BTree.BTreeUtil.InternalNode;
import BTree.BTreeUtil.LeafNode;
import Database.DBRepository;
import static Database.DBUtil.Constants.*;

// Data File Design:
// 60 bytes per record * 4
// 1 byte to mark it is a data file (byte 245)
// 4 bytes for record slot availability (byte 246 - 249)
// 5 bytes for next block number (byte 250 - 254)
// 1 byte for data file marker (byte 255)
// 251 in total
public class DataIndexFileWriter {

    private DBRepository repo;

    private FSMReaderWriter fsmReaderWriter;

    private BTree bTree;

    public DataIndexFileWriter(String tableName, String databaseName) {
        this.repo = new DBRepository(databaseName);
        this.fsmReaderWriter = new FSMReaderWriter(databaseName);
        this.bTree = new BTree();
    }

    // Read the data from the csv tableFile and write it into the PFSFiles
    //    int PFSFileNum, int offset, int blockNum, String content

    // Write the entire table into the block
    public void writeDataFile(String tableName) {
        File tableFile = new File(TABLE_DIRECTORY + "/" + tableName);
        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            List<Integer> blockNums = new ArrayList<>();
            int blockNum = -1;
            int currentBlockNum;
            int PFSFileNum = 0;
            int slotNum;
            String line;
            Queue<Integer> slotNums = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                if (line.length() > RECORD_SIZE) {
                    line = line.substring(0, RECORD_SIZE);
                }
                if (slotNums.isEmpty()) {
                    blockNum = fsmReaderWriter.getNextAvailableBlock();
                    System.out.println("next available block: " + blockNum);
                    blockNums.add(blockNum);
                    slotNums = new LinkedList<>(Arrays.asList(0, 1, 2, 3));
                }
                slotNum = slotNums.poll();
                currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
                PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
                if (repo.readChar(PFSFileNum, RECORD_SLOT_OFFSET, currentBlockNum).equals(" ")) {
                    initializeDataFile(blockNum);
                }
                System.out.println("slotNum: " + slotNum);
                writeDataRecord(line, blockNum, slotNum);
                int id = getRecordId(line);
                bTree.insert(id, blockNum);
                System.out.println("blockNums: " + blockNums);
            }
            int lastBlockNum = blockNums.get(blockNums.size() - 1);
            for (int i = 0; i < blockNums.size(); i++) {
                if (i != blockNums.size() - 1) {
                    writeNextBlockNum(blockNums.get(i), blockNums.get(i + 1));
                } else {
                    repo.write(PFSFileNum, NEXT_BLOCK_NUM_OFFSET, lastBlockNum % BLOCK_NUM_PER_FILE, END_OF_FILE);
                }
            }
            fsmReaderWriter.setAvailability(lastBlockNum, false);
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
    }

    public void writeIndexFile() {
        try {
            HashSet<BTreeNode> nodes = bTree.getNodes();
            HashMap<Integer, Integer> keyBlock = bTree.getKeyBlock();
            HashMap<BTreeNode, Integer> nodeBlockNums = new HashMap<>();
            for (BTreeNode node : nodes) {
                int blockNum = fsmReaderWriter.getNextAvailableBlock();
                nodeBlockNums.put(node, blockNum);
                int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
                int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
                String serializedNode = serializeBTreeNode(node, keyBlock, nodeBlockNums);
                repo.write(PFSFileNum, 0, currentBlockNum, serializedNode);
                repo.write(PFSFileNum, 250, currentBlockNum, blockNumTo5Digits(blockNum));
                repo.write(PFSFileNum, 255, currentBlockNum, INDEX_MARKER);
            }
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
    }

    public String serializeBTreeNode(BTreeNode node, HashMap <Integer, Integer> keyBlock, HashMap <BTreeNode, Integer> nodeBlockNums) {
        if (node instanceof InternalNode) {
            return serializeBTreeNode((InternalNode) node, keyBlock, nodeBlockNums);
        } else {
            return serializeBTreeNode((LeafNode) node, keyBlock, nodeBlockNums);
        }
    }

    public String serializeBTreeNode(InternalNode node, HashMap <Integer, Integer> keyBlock, HashMap <BTreeNode, Integer> nodeBlockNums) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < node.getKeys().size(); i++) {
            String childBlockNum = blockNumTo5Digits(nodeBlockNums.get(node.getChildren().get(i)));
            String key = keyTo8Digits(node.getKeys().get(i));
            String keyBlockNum = blockNumTo5Digits(keyBlock.get(node.getKeys().get(i)));
            result.append(childBlockNum).append(key).append(keyBlockNum);
        }
        result.append(blockNumTo5Digits(nodeBlockNums.get(node.getChildren().get(node.getChildren().size() - 1))));
        return result.toString();
    }

    public String serializeBTreeNode(LeafNode node, HashMap <Integer, Integer> keyBlock, HashMap <BTreeNode, Integer> nodeBlockNums) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < node.getKeys().size(); i++) {
            String key = keyTo8Digits(node.getKeys().get(i));
            String keyBlockNum = blockNumTo5Digits(keyBlock.get(node.getKeys().get(i)));
            result.append(NULL_NODE_NUM).append(key).append(keyBlockNum);
        }
        return result.toString();
    }

    // Write a record into the block
    public void writeDataRecord(String record, int blockNum, int slotNum) {
        try {
            int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
            int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
            repo.write(PFSFileNum, slotNum * RECORD_SIZE, currentBlockNum, record);
            repo.write(PFSFileNum, RECORD_SLOT_OFFSET + slotNum, currentBlockNum, "1");
            if (repo.read(PFSFileNum, RECORD_SLOT_OFFSET, currentBlockNum, RECORD_SLOT_SIZE).equals("1111")) {
                fsmReaderWriter.setAvailability(blockNum, false);
            }
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
    }

    public void writeNextBlockNum(int blockNum, int nextBlockNum) {
        try {
            int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
            int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
            repo.write(PFSFileNum, 250, currentBlockNum, blockNumTo5Digits(nextBlockNum));
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
    }

    public void initializeDataFile(int blockNum) {
        try {
            int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
            int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
            repo.write(PFSFileNum, 246, currentBlockNum, "0000");
            repo.write(PFSFileNum, 255, currentBlockNum, DATA_MARKER);
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
    }

    public String blockNumTo5Digits(int blockNum) {
        StringBuilder result = new StringBuilder(String.valueOf(blockNum));
        while (result.length() < 5) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    public String keyTo8Digits(int key) {
        StringBuilder result = new StringBuilder(String.valueOf(key));
        while (result.length() < 8) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    private int getAvailableSlot(int blockNum) {
        try {
            int PFSFileNum = blockNum / BLOCK_NUM_PER_FILE;
            int currentBlockNum = blockNum % BLOCK_NUM_PER_FILE;
            String availability = repo.read(PFSFileNum, RECORD_SLOT_OFFSET, currentBlockNum, RECORD_SLOT_SIZE);
            for (int i = 0; i < availability.length(); i++) {
                if (availability.charAt(i) == '0') {
                    return i;
                }
            }
        } catch (IOException e) {
            Logger.getLogger(DataIndexFileWriter.class.getName()).severe(e.getMessage());
        }
        return -1;
    }

    private int getRecordId(String record) {
        return Integer.parseInt(record.split(",")[0]);
    }
}
