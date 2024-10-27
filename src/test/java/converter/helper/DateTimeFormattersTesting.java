package converter.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import converter.helper.baseLinker.DateTimeFormattersBaseLinkerPromo;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateTimeFormattersTesting {

  private static DateTimeFunctions dateTimeFormatterBaseLinkerPromo;

  @Test
  void convertGetDateAndTimeFunctionShouldConvertToPromoDateFormat() throws ParseException {
    //given
    String given = "16.11.2022 21:04:50";
    String expected = "2022-11-16T21:04:50";

    //when
    String actual = DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToPromoDateTime(
        given);

    //then
    assertEquals(actual, expected);
  }

  @ParameterizedTest
  @CsvSource({"Europe/Warsaw"})
  void convertGetDateAndTimeFunctionShouldConvertToPromoDateFormatWithTimeZoneSwitching(
      String timezone) throws ParseException {
    //given
    String input = "16.11.2022 21:04:50";
    String expected = "2022-11-16T22:04:50";

    //when
    String actual = DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToPromoDateTime(
        input, ZoneId.of(timezone));

    //then
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({"Europe/Warsaw"})
  void convertTimeZoneBaseLinkerDateTimeShouldChangeHour(String timezone) throws ParseException {
    //given
    String given = "16.11.2022 21:04:50";
    String expected = "16.11.2022 22:04:50";

    //when
    String actual = DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToDateTimeBaseLinkerWithDifferentTimeZone(
        given, ZoneId.of(timezone));

    //then
    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({"Europe/Warsaw"})
  void convertTimeZoneBaseLinkerDateTimeShouldChangeDayAndHour(String timezone)
      throws ParseException {
    //given
    String given = "16.11.2022 23:04:50";
    String expected = "17.11.2022 00:04:50";

    //when
    String actual = DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToDateTimeBaseLinkerWithDifferentTimeZone(
        given, ZoneId.of(timezone));

    //then
    assertEquals(expected, actual);
  }

  @Test
  void convertUnixTimeStampToLocalDateTimeTest() throws ParseException {
    //given
    LocalDateTime expected = LocalDateTime.of(2022, 5, 13, 12, 11, 55)
        .atZone(ZoneId.of("Europe/Warsaw")).toLocalDateTime();
    Long given = 1652436715L;

    //when
    LocalDateTime actual = DateTimeFunctions.convertUnixTimeStampToLocalDateTime(given,
        ZoneId.of("Europe/Warsaw"));

    //then
    assertEquals(actual, expected);
  }

  @Test
  void convertLocalDateTimeToUnixTimeStamp() throws ParseException {
    //given
    LocalDateTime given = LocalDateTime.of(2022, 5, 13, 12, 11, 55)
        .atZone(ZoneId.of("Europe/Warsaw")).toLocalDateTime();
    Long expected = 1652436715L;

    //when
    Long actual = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(given,
        ZoneId.of("Europe/Warsaw"));

    //then
    assertEquals(expected.toString(), StringProcessor.extractTenDigits(actual.longValue()));
  }
}
