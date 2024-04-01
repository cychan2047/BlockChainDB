package Database.DBUtil;

public final class Constants {
    public static final int TABLE_NAME_OFFSET = 0;
    public static final int TABLE_SIZE_OFFSET = 50;
    public static final int TABLE_TIME_OFFSET = 60;
    public static final int STARTING_DATA_BLOCK_OFFSET = 80;
    public static final int ROOT_INDEX_BLOCK_OFFSET = 90;
    public static final int USED_DATA_BLOCK_COUNT = 100;
    public static final int REMARK_OFFSET = 110;
    public static final int FCB_BLOCK_NUM = 0;

    public static final int BLOCK_SIZE = 256;
    public static final int FILE_SIZE = 1024 * 1024;

    public static final int METADATA_PFS_FILE_NUM = 0;
    public static final int METADATA_BLOCK_NUM = 0;

    public static final int DB_NAME_OFFSET = 0;
    public static final int DB_SIZE_OFFSET = 50;
    public static final int PFS_FILE_COUNT_OFFSET = 60;
    public static final int BLOCK_SIZE_OFFSET = 70;
    public static final int KV_TABLE_COUNT = 80;

    public static final String DATABASE_DIRECTORY = "./src/Database/PFSFiles";
    public static final String TABLE_DIRECTORY = "./src/Database/KVTables";

    public static final int FSM_BLOCK_NUM = 9;
    public static final int FSM_BLOCK_OFFSET = 0;
    public static final int FSM_PFS_FILE_NUM = 0;
}
