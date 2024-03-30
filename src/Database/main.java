package Database;

public class main {

    public static void main(String[] args) {
      
        DBController controller = new DBController();
        controller.startCLI();
        DBRepository dbRepository = new DBRepository(256, 1024 * 1024, "./src/Database/PFSFiles");
        dbRepository.createPFSFile(0, "test");
        dbRepository.printTestFile("./src/Database/PFSFiles", "test.db0", "output.txt");

    }
}
