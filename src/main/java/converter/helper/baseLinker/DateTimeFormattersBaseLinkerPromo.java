package converter.helper.baseLinker;

import converter.helper.DateFormatsConstants;
import converter.helper.DateTimeFunctions;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeFormattersBaseLinkerPromo {

  public static String convertDateTimeBaseLinkerToPromoDateTime(String input)
      throws ParseException {
    LocalDateTime formattedTime = LocalDateTime.parse(input,
        DateFormatsConstants.dateTimeBaseLinkerFormat);
    return DateFormatsConstants.dateTimePromoFormat.format(formattedTime).replace(" ", "T");
  }

  public static String convertLocalDateTimeToBaseLinkerFormatDateTime(LocalDateTime input)
      throws ParseException {
    return DateFormatsConstants.dateTimeBaseLinkerFormat.format(input);
  }

  public static String convertDateTimeBaseLinkerToPromoDateTime(String input, ZoneId zoneId)
      throws ParseException {
    LocalDateTime formattedDateTime = LocalDateTime.parse(input,
        DateFormatsConstants.dateTimeBaseLinkerFormat);
    formattedDateTime = DateTimeFunctions.switchTimeZone(formattedDateTime,
        ZoneId.of("Europe/London"), zoneId);
    return DateFormatsConstants.dateTimePromoFormat.format(formattedDateTime).replace(" ", "T");
  }

  public static String convertDateTimeBaseLinkerToDateTimeBaseLinkerWithDifferentTimeZone(
      String input, ZoneId zoneId) throws ParseException {
    LocalDateTime formattedDateTime = LocalDateTime.parse(input,
        DateFormatsConstants.dateTimeBaseLinkerFormat);
    formattedDateTime = DateTimeFunctions.switchTimeZone(formattedDateTime,
        ZoneId.of("Europe/London"), zoneId);
    return DateFormatsConstants.dateTimeBaseLinkerFormat.format(formattedDateTime);
  }
}
