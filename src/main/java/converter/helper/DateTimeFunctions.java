package converter.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeFunctions {

  private static final Logger log
      = LoggerFactory.getLogger(DateTimeFunctions.class);

  public static LocalDateTime convertUnixTimeStampToLocalDateTime(Long input, ZoneId zoneId) {
    LocalDateTime triggerTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(input), zoneId);
    return triggerTime;
  }

  public static LocalDateTime convertUnixTimeStampToLocalDateTime(Long input) {
    LocalDateTime triggerTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(input), ZoneId.of("Europe/London"));
    return triggerTime;
  }

  public static LocalDateTime switchTimeZone(LocalDateTime oldDateTime, ZoneId oldZoneId,
      ZoneId newZoneId) {
    return oldDateTime.atZone(oldZoneId)
        .withZoneSameInstant(newZoneId)
        .toLocalDateTime();
  }

  public static long convertLocalDateTimeToUnixTimeStamp(LocalDateTime localDateTime,
      ZoneId zoneId) {
    return localDateTime.atZone(zoneId)
        .toInstant().toEpochMilli();
  }

  public static long convertLocalDateTimeToUnixTimeStamp(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.of("Europe/London")).toInstant().toEpochMilli();
  }

}
