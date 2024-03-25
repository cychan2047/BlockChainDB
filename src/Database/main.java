package Database;

public class main {

    public static void main(String[] args) {

        DBController controller = new DBController();
        controller.startCLI();


        DBRepository dbRepository = new DBRepository(256, 1024 * 1024);
        dbRepository.createPFSFile(0, "test", "./src/Database/PFSFiles");
    }
}
