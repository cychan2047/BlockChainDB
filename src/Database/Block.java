package Database;

import java.util.List;

public class Block {
  private static final int BLOCK_SIZE = 256;

  private IndexFile index;
  private List<DataFile> dataFiles;

  public static int getBlockSize() {
    return BLOCK_SIZE;
  }
}
