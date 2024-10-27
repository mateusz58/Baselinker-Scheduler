package converter.other;

import static org.junit.jupiter.api.Assertions.assertEquals;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.helper.DateFormatsConstants;
import converter.helper.FileHelper;
import converter.helper.StringProcessor;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class DateTimeProcessingTests {


  @Test
  void extractDateTimeFromFileName() {
    String given = "XML_zamwienia_2022_12_10_23_23.xml";
    String expected = "2022_12_10_23_23";

    String actual = Pattern.compile("([0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2})").matcher(given)
        .results().map(i -> i.group(1)).collect(Collectors.joining());

    assertEquals(expected, actual);
  }

  @Test
  void extractLocalDateTimeFromString() {
    String given = "2022_12_10_23_23";
    LocalDateTime expected = LocalDateTime.of(2022, 12, 10, 23, 23);

    LocalDateTime actual = LocalDateTime.parse(given,
        DateFormatsConstants.dateTimeBaseLinkerFileFormat);

    assertEquals(expected, actual);
  }

  @Test
  void getLatestOrderDateFromXmlFile() throws IOException {
    List<String> given = FileHelper.listAllFilesFromDirectory(
        FILES_BASELINKER_TESTING.ORDERS_PROCESSED_DIRECTORY);
    LocalDateTime expected = LocalDateTime.of(2022, 12, 19, 11, 23, 46);

    LocalDateTime actual = given.stream()
        .map(i -> StringProcessor.extractLocalDateTimeFromFileName(i))
        .max((o1, o2) -> o1.compareTo(o2)).get();

    assertEquals(expected, actual);
  }
}