package Database;

import java.io.*;
import java.util.logging.Logger;

public class DBRepository {

    private int blockSize;

    private int fileSize;

    public DBRepository(int blockSize, int fileSize) {
        this.blockSize = blockSize;
        this.fileSize = fileSize;
    }

    public void createPFSFile(int PFSFileCount, String databaseName, String directory) {

        String currentPath = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + currentPath);

        // Create a new file
        String name = databaseName + ".db" + PFSFileCount;
        File file = new File(directory, name);
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

    public void PFSFileToString(String pathname) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathname))) {
            char[] buffer = new char[blockSize];
            while (reader.read(buffer) != -1) {
                System.out.println(new String(buffer));
            }
        } catch (IOException e) {
            Logger.getLogger(DBRepository.class.getName()).severe(e.getMessage());
        }
    }

}
