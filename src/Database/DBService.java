package Database;

public class DBService {
    private int PFSFileCount;

    private DBRepository dbRepository;

    public DBService() {
        this.PFSFileCount = 0;
        this.dbRepository = new DBRepository(256, 1024 * 1024);
    }

    public DBService(int PFSFileCount) {
        this.PFSFileCount = PFSFileCount;
        this.dbRepository = new DBRepository(256, 1024 * 1024);
    }

    public void open() {};

    public void put(String pathname) {};

    public void get() {};

    public void rm() {};

    public void dir() {};

    public void find(int key) {};

    public void putr(String pathname, String remark) {};

    public void kill() {};

    public void quit() {};

}
