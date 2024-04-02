package Database;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class DBRepository {

    private String databaseName;

    public DBRepository(String databaseName) {
        this.databaseName = databaseName;
    }

    // Directory: the relative path from the root directory
    //     Example: "./src/Database/PFSFiles"
    // Creates a PFS file and initializes it with empty blocks
    public void createPFSFile(int PFSFileCount) {
        // Create a new file
        String name = databaseName + ".db" + PFSFileCount;
        File file = new File(DATABASE_DIRECTORY, name);
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }

        // Fill the file with empty blocks to initialize it
        int numBlocks = FILE_SIZE / BLOCK_SIZE;
        char[] blockContent = new char[BLOCK_SIZE];
        Arrays.fill(blockContent, ' ');
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < numBlocks; i++) {
                writer.write(blockContent);
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

    // Prints the contents of a test file
    public void printTestFile(String directory, String inputTestFile, String outputTestFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(directory + "/" + inputTestFile));
             FileWriter writer = new FileWriter(directory + "/" + outputTestFile)) {
            char[] buffer = new char[BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
                writer.write('\n'); // Write a newline character after each buffer
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

    // Writes a string to a specific block in a PFS file
    public void write(int PFSFileNum, int offset, int blockNum, String content) throws IOException, IllegalArgumentException {
        String pathname = DATABASE_DIRECTORY + "/" + databaseName + ".db" + PFSFileNum;

        if (offset + content.length() > BLOCK_SIZE) {
            throw new IllegalArgumentException("Content exceeds block size.");
        }

        try (RandomAccessFile file = new RandomAccessFile(pathname, "rw")) {
            file.seek(offset + blockNum * BLOCK_SIZE);
            file.writeChars(content);
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

    // Reads a specific block from a PFS file
    public String readBlock(int PFSFileCount, int offset, int blockNum) throws IOException {
        return read(PFSFileCount, offset, blockNum, BLOCK_SIZE);
    }

    // Reads a single character from a specific location in a PFS file
    public String readChar(int PFSFileCount, int offset, int blockNum) throws IOException {
        return read(PFSFileCount, offset, blockNum, 1);
    }

    public String read(int PFSFileCount, int offset, int blockNum, int length) throws IOException {
        String pathname = DATABASE_DIRECTORY + "/" + databaseName + ".db" + PFSFileCount;
        StringBuilder content = new StringBuilder();
        try (RandomAccessFile file = new RandomAccessFile(pathname, "r")) {
            file.seek(offset + blockNum * BLOCK_SIZE);
            for (int i = 0; i < length; i++) {
                content.append(file.readChar());
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
        return content.toString();
    }

    // Deletes a PFS file
    public void delete(int PFSFileCount) {
        String pathname = DATABASE_DIRECTORY + "/" + databaseName + ".db" + PFSFileCount;
        File file = new File(pathname);
        if (file.delete()) {
            System.out.println("File deleted: " + file.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }

}
