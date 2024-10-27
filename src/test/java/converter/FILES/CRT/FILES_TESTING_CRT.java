package converter.FILES.CRT;

import java.io.File;
import java.nio.file.Path;

public class FILES_TESTING_CRT {

  public static final String CRT_EVENTS_directory = new File(
      "src/test/resources/crt/events/").getPath();
  //FILES
  //test files and directories
  public static File CRT_JSON = new File("src/test/resources/crt/crt_api.json");
  public static File CRT_PROMO = new File("src/test/resources/crt/crt_api.json");
  public static File CRT_JSON_MANY_ORDERS = new File("src/test/resources/crt/crt_many_orders.json");
  public static File CRT_JSON_EVENT = new File("src/test/resources/crt/crt_event.json");
  public static File CRT_OUTPUT_JSON = new File("src/test/resources/crt/output.json");
  public static File CRT_OUTPUT_XML = new File("src/test/resources/crt/output.xml");
  public static Path CRT_EVENTS_DIRECTORY_SAMPLES = Path.of("src/test/resources/crt/events/");
  public static Path CRT_EVENTS_OUTPUT_DIRECTORY = Path.of("src/test/resources/crt/output.xml");
  public static File CRT_DATABABASE_ORDER = new File(
      "src/test/resources/crt/database/database_record.json");
  public static File PROMO_FILE_CRT = new File("src/test/resources/crt/promo-CRT.xml");

  private FILES_TESTING_CRT() {
  }
}
