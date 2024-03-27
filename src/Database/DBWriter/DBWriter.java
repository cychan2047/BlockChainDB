package Database.DBWriter;

public abstract class DBWriter {

    private String dataFileDirectory;

    public DBWriter(String dataFileDirectory) {
        this.dataFileDirectory = dataFileDirectory;
    }
}
