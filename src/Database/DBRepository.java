package Database;

import java.io.*;
import java.util.logging.Logger;

public class DBRepository {

    private int blockSize;

    private int fileSize;

    private String databaseDirectory;

    public DBRepository(int blockSize, int fileSize, String databaseDirectory) {
        this.blockSize = blockSize;
        this.fileSize = fileSize;
        this.databaseDirectory = databaseDirectory;
    }

    // Directory: the relative path from the root directory
    //     Example: "./src/Database/PFSFiles"
    public void createPFSFile(int PFSFileCount, String databaseName) {

        String currentPath = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + currentPath);

        // Create a new file
        String name = databaseName + ".db" + PFSFileCount;
        File file = new File(databaseDirectory, name);
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File created: " + file.getName());
                setCurrentPath(databaseDirectory + "/" + name);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }

        // Initialize the file
        int numBlocks = fileSize / blockSize;
        char[] blockContent = new char[blockSize];
        for (int i = 0; i < blockSize; i++) {
            blockContent[i] = ' ';
        }
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
            char[] buffer = new char[blockSize];
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

        if (offset + content.length() > blockSize) {
            throw new IllegalArgumentException("Content exceeds block size.");
        }

        try (RandomAccessFile file = new RandomAccessFile(pathname, "rw")) {
            file.seek(offset + blockNum * blockSize);
            file.writeChars(content);
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

    public String readBlock(String databaseName, int PFSFileCount, int offset, int blockNum) throws IOException {
        String pathname = databaseDirectory + "/" + databaseName + ".db" + PFSFileCount;
        StringBuilder content = new StringBuilder();

        try (RandomAccessFile file = new RandomAccessFile(pathname, "r")) {
            file.seek(offset + blockNum * blockSize);
            for (int i = 0; i < blockSize; i++) {
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
            file.seek(offset + blockNum * blockSize);
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

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getCurrentPath() {
        return databaseDirectory;
    }

    public void setCurrentPath(String currentPath) {
        this.databaseDirectory = currentPath;
    }


}
