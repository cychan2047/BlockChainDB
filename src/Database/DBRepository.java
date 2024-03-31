package Database;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;
import static Database.DBUtil.Constants.*;

public class DBRepository {

    public DBRepository() {}

    // Directory: the relative path from the root directory
    //     Example: "./src/Database/PFSFiles"
    public void createPFSFile(int PFSFileCount, String databaseName) {
        // Create a new file
        String name = databaseName + ".db" + PFSFileCount;
        File file = new File(databaseDirectory, name);
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

        // Initialize the file
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
    public void write(String databaseName, int PFSFileNum, int offset, int blockNum, String content) throws IOException, IllegalArgumentException {
        String pathname = databaseDirectory + "/" + databaseName + ".db" + PFSFileNum;

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

    public String readBlock(String databaseName, int PFSFileCount, int offset, int blockNum) throws IOException {
        String pathname = databaseDirectory + "/" + databaseName + ".db" + PFSFileCount;
        StringBuilder content = new StringBuilder();

        try (RandomAccessFile file = new RandomAccessFile(pathname, "r")) {
            file.seek(offset + blockNum * BLOCK_SIZE);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                content.append(file.readChar());
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }

        return content.toString();
    }

    public String readChar(String databaseName, int PFSFileCount, int offset, int blockNum) throws IOException {
        String pathname = databaseDirectory + "/" + databaseName + ".db" + PFSFileCount;
        StringBuilder content = new StringBuilder();
        try (RandomAccessFile file = new RandomAccessFile(pathname, "r")) {
            file.seek(offset + blockNum * BLOCK_SIZE);
            content.append(file.readChar());
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
        return content.toString();
    }
    public void delete(String databaseName, int PFSFileCount) {
        String pathname = databaseDirectory + "/" + databaseName + ".db" + PFSFileCount;
        File file = new File(pathname);
        if (file.delete()) {
            System.out.println("File deleted: " + file.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }
}
