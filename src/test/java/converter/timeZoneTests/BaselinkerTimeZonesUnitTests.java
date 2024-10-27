package converter.timeZoneTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import converter.helper.DateTimeFunctions;
import converter.helper.StringProcessor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

public class BaselinkerTimeZonesUnitTests {

  @Test
  void convertWarsawTimeToLondontimeShouldReturnSubstractedTime() {
    Long given = 1669204603L; // 23.11.2022 11:56:43 Warsaw Time
    Long expected = 1669201003L; // 23.11.2022 10:56:43 London Time
    Long actual;

    LocalDateTime localDateTime = DateTimeFunctions.convertUnixTimeStampToLocalDateTime(given,
        ZoneId.of("Europe/London"));
    localDateTime = DateTimeFunctions.switchTimeZone(localDateTime, ZoneId.of("Europe/Warsaw"),
        ZoneId.of("Europe/London"));
    actual = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(localDateTime);

    assertEquals(StringProcessor.extractTenDigits(expected),
        StringProcessor.extractTenDigits(actual));
  }

  @Test
  void convertGmtToGmtShouldReturnSameResult() {
    Long given = 1669201003L; // 23.11.2022 10:56:43 London time
    Long expected = 1669201003L; // 23.11.2022 11:56:43 Warsaw Time
    Long actual;

    LocalDateTime localDateTime = DateTimeFunctions.convertUnixTimeStampToLocalDateTime(given,
        ZoneId.of("Europe/London"));
    actual = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(localDateTime);

    assertEquals(StringProcessor.extractTenDigits(expected),
        StringProcessor.extractTenDigits(actual));
  }

  @Test
  void convertGmtToWarsawTimeZoneWithGivenUnixTimeStamp() {
    Long given = 1669201003L; // 23.11.2022 10:56 London Timee
    Long expected = 1669204603L; // 23.11.2022 11:56:43 Warsaw Time
    Long actual;

    LocalDateTime localDateTime = DateTimeFunctions.convertUnixTimeStampToLocalDateTime(given,
        ZoneId.of("Europe/Warsaw"));
    actual = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(localDateTime);

    assertEquals(StringProcessor.extractTenDigits(expected),
        StringProcessor.extractTenDigits(actual));
  }
}
