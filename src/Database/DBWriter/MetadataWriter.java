package Database.DBWriter;

import java.io.*;

public class MetadataWriter extends DBWriter {

    private static final int BLOCK_NUM = 0;
    private static final int DB_NAME_OFFSET = 0;
    private static final int DB_SIZE_OFFSET = 50;
    private static final int PFS_FILE_COUNT_OFFSET = 60;
    private static final int BLOCK_SIZE_OFFSET = 70;
    private static final int KV_TABLE_COUNT = 80;


    private String dataFileDirectory;
    public MetadataWriter(String dataFileDirectory) {
        super(dataFileDirectory);
    }

    public void write(String name, int PFSFileSize, int PFSFileCount, int blockSize, int kvTableCount) throws IOException {
        File file = new File(dataFileDirectory);
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
