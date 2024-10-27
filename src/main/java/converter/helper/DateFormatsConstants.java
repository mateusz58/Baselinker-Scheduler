package converter.helper;

import java.time.format.DateTimeFormatter;

public class DateFormatsConstants {

  public static final DateTimeFormatter datePromoFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter dateTimePromoFormat = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");
  public static final DateTimeFormatter dateBaseLinkerFormat = DateTimeFormatter.ofPattern(
      "dd.MM.yyyy");
  public static final DateTimeFormatter dateTimeBaseLinkerFormat = DateTimeFormatter.ofPattern(
      "dd.MM.yyyy HH:mm:ss");
  public static final DateTimeFormatter dateTimeBaseLinkerFileFormat = DateTimeFormatter.ofPattern(
      "yyyy_MM_dd_HH_mm");
  public static final DateTimeFormatter dateTimeBaseLinkerFileFormatWithSeconds = DateTimeFormatter.ofPattern(
      "yyyy_MM_dd_HH_mm_ss");
}
