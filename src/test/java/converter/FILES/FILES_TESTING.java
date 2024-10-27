package converter.FILES;

import java.io.File;

public class FILES_TESTING {

  //FILES_TESTING_FILE_HELPER
  //test files and directories
  public static final String FILE_HELPER_GIVEN_DIRECTORY = new File(
      "src/test/resources/FILE_HELPER/given").getPath();
  public static final String FILE_HELPER_ACTUAL_DIRECTORY = new File(
      "src/test/resources/FILE_HELPER/actual").getPath();
  public static final String FILE_HELPER_EXPECTED_DIRECTORY = new File(
      "src/test/resources/FILE_HELPER/expected").getPath();

  public static final File PROMO_FILE = new File("src/test/resources/promo_standard.xml");
}
