package Database;

/*
 * Index structure: B Tree
 * Test data: movies-small.csv
 * Key: Integer
 * Size of record: 64 bytes
 * Allocation method: Linked Allocation
 * Free block method: Bitmap
 */

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import BTree.BTree;
import java.util.List;

public class BlockchainDB {
  static final int BLOCK_SIZE = 256;
  static final int INITIAL_SIZE = 1024 * 1024; // 1024 KB

  File dbFile;
  List<DBFile> files = new ArrayList<>();
  BitSet freeBlocks;
  BTree indexTree;
}
