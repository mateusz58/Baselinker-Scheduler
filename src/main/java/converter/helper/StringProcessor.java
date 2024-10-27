package converter.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import converter.configuration.ObjectMapperInstance;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringProcessor {

  public static String replacePolishCharacters(String input) {
    String output = input.replace('ń', 'n')
        .replace('ó', 'o')
        .replace('ą', 'a')
        .replace('ć', 'c')
        .replace('ś', 's')
        .replace('ż', 'z')
        .replace('ź', 'z')
        .replace('ę', 'e')
        .replace('ó', 'o')
        .replace('ł', 'l')
        .replace('Ń', 'N')
        .replace('Ó', 'O')
        .replace('Ą', 'A')
        .replace('Ć', 'C')
        .replace('Ś', 'S')
        .replace('Ż', 'Z')
        .replace('Ź', 'z')
        .replace('Ę', 'E')
        .replace('Ó', 'O')
        .replace('Ł', 'L');
    return output;
  }

  public static String removePreffixStreetName(String input) {
    return input.replaceAll("ul\\.\\s*", "");
  }

  public static String convertMapToJson(Map<?, ?> map) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      if (i == 0) {
        stringBuilder.append("{");
      }
      if (i == map.size() - 1) {
        stringBuilder.append("\"" + entry.getKey() + "\":" + entry.getValue());
        stringBuilder.append("}");
        break;
      }
      stringBuilder.append("\"" + entry.getKey() + "\":" + entry.getValue() + ",");
      i++;
    }
    return stringBuilder.toString();
  }

  public static String extractTenDigits(Long value) {
    return Pattern.compile("([1-9][0-9]{9})").matcher(value.toString()).results()
        .map(i -> i.group().toString()).collect(Collectors.joining());
  }

  public static LocalDateTime extractLocalDateTimeFromFileName(String fileName) {
    String resultDateTimeWithoutSeconds = Pattern.compile(
            "([0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2})(_[0-9]{2})?").matcher(fileName).results()
        .map(i -> i.group(1)).collect(Collectors.joining());
    String seconds = Pattern.compile("([0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2})(_[0-9]{2})?")
        .matcher(fileName).results().map(i -> i.group(2)).collect(Collectors.joining());
    if (seconds.equals("null")) {
      return LocalDateTime.parse(resultDateTimeWithoutSeconds,
          DateFormatsConstants.dateTimeBaseLinkerFileFormat);
    }
    return LocalDateTime.parse(resultDateTimeWithoutSeconds + seconds,
        DateFormatsConstants.dateTimeBaseLinkerFileFormatWithSeconds);
  }

  public static String convertObjectToJson(Object object) throws JsonProcessingException {
    ObjectMapper objectMapper = ObjectMapperInstance.getInstanceJson().copy();
    return objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .writeValueAsString(object);
  }

  public static String obtainValueFromSelectedFieldInStringObject(String input, String key) {
    String regex = String.format("(%s)=([\\w]+),", key);
    return Pattern.compile(regex).matcher(input).results().map(s -> s.group(2)).findFirst()
        .orElse("DEFAULT");
  }

  public static String extractValueFromSelectedFieldInBaseLinkerOrConnectedRetail(String key,
      String object) {
    String regex = String.format("(%s)=((BL[0-9]+)||([1-9]+-[0-9]{2})),", key);
    return Pattern.compile(regex).matcher(object).results().map(s -> s.group(2)).findFirst()
        .orElse("DEFAULT");
  }
}
