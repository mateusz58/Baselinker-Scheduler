package converter.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StringProcessorTest {

  StringProcessorTest processorTest;

  @Test
  void testingPolishCharacterRemoval() {
    //given
    String given = "óółęćń";
    String expected = "oolecn";
    //when
    String actual = StringProcessor.replacePolishCharacters(given);
    //then
    assertEquals(expected, actual);
  }

  @Test
  void testRemoveStreet() {
    //given
    String given = "ul. Karkonoska 26";
    String expected = "Karkonoska 26";
    //when
    String actual = given.replaceAll("ul\\.\\s+", "");
    //then
    assertEquals(expected, actual);
  }

  @Test
  void testMapToString() {
    //given
    Map<String, String> given = new LinkedHashMap<>();
    given.put("k1", "v1");
    given.put("k2", "v2");
    given.put("k3", "v3");
//        given = Map.of("k1","v1","k2","v2","k3","v3");
    String expected = "{\"k1\":v1,\"k2\":v2,\"k3\":v3}";
    //when
    String actual = StringProcessor.convertMapToJson(given);
    //then
    assertEquals(expected, actual);
  }

  @Test
  void testExtractFirstTenDigits() {
    Long given = 1000000000000000L;
    String expected = "1000000000";

    String actual = StringProcessor.extractTenDigits(given);

    assertEquals(expected, actual);
  }

  @Test
  @Disabled
  void testConvertObjectToJson() throws JsonProcessingException {
    OrderBaseLinkerJson given = OrderBaseLinkerJson.builder().address("address_custom").build();
    String expected = "{\n" +
        "  \"address\" : \"address_custom\"\n" +
        "}";

    String actual = StringProcessor.convertObjectToJson(given);

    assertEquals(expected, actual);
  }
}
