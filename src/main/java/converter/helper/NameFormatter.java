package converter.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameFormatter {

  private static final Logger log
      = LoggerFactory.getLogger(NameFormatter.class);

  public static String getFirstName(String input) {
    if (input == null) {
      log.error("Could not load parameter with null value");
      throw new IllegalArgumentException();
    }
    return input.split(" ", input.length())[0];
  }

  public static String getLastName(String input) {
    if (input == null) {
      log.error("Could not load parameter with null value");
      throw new IllegalArgumentException();
    }
    String[] array = input.split(" ", input.length());
    return array[array.length - 1];
  }
}
