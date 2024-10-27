package converter.exceptions.baselinker;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IncorrectRequestException extends IOException {

  private static final String message = "Incorrect request parametres";

  public IncorrectRequestException() {
    super(message);
  }

  public IncorrectRequestException(String message) {
    super(Pattern.compile("\"(error_code)\":\"([A-Z_]+)\"").matcher(message).results()
        .map(i -> i.group(2)).collect(Collectors.joining()));
  }
}
