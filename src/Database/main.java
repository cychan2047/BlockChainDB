package Database;

public class main {

    public static void main(String[] args) {
      
        DBController controller = new DBController();
        controller.startCLI();
        DBRepository dbRepository = new DBRepository();
        dbRepository.createPFSFile(0, "test");
        dbRepository.printTestFile("./src/Database/PFSFiles", "test.db0", "output.txt");

    }
}
