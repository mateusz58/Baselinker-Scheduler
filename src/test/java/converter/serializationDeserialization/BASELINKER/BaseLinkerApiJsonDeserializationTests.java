package converter.serializationDeserialization.BASELINKER;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.helper.FileHelper;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BaseLinkerApiJsonDeserializationTests {

  ObjectSerializer serialize;
  ObjectsDeserializer deserialize;

  @BeforeEach
  void beforeEach() throws IOException {
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON));
  }

  @Test
  void deserializationOfJsonShouldNotThrowAnyException() throws IOException {
    File given = FILES_BASELINKER_TESTING.BASE_LINKER_JSON;
    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    //then
    assertTrue(deserializedJsonApiObject.getClass().isAssignableFrom(OrdersBaseLinkerJson.class));
  }

  @ParameterizedTest
  @CsvSource({"sonski_77, Zgierz, PL"})
  void deserializationOfJsonShouldMatchValueOfSomePassedFieldsAndShouldNotThrowAnyException(
      String clientLogin, String addressCity, String addressCountryCode) throws IOException {
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON.getAbsolutePath());
    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    //then

    assertTrue(deserializedJsonApiObject.getClass().isAssignableFrom(OrdersBaseLinkerJson.class));
    assertEquals(clientLogin, deserializedJsonApiObject.getOrders().get(0).getClientLogin());
    assertEquals(addressCity, deserializedJsonApiObject.getOrders().get(0).getAddressCity());
    assertEquals(addressCountryCode,
        deserializedJsonApiObject.getOrders().get(0).getAddressCountryCode());
  }

  @ParameterizedTest
  @CsvSource({"1, 12911880143, 5 PACK - Boxer shorts DreiMaster L, 4063523845332, 1"})
  void deserializationOfJsonShouldMatchValueOfAllRowFieldsndShouldNotThrowAnyException(
      BigDecimal priceBrutto, String auctionId, String name, String ean, Integer quantity)
      throws IOException {
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON.getAbsolutePath());

    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);

    //then
    assertTrue(deserializedJsonApiObject.getClass().isAssignableFrom(OrdersBaseLinkerJson.class));
    assertEquals(priceBrutto,
        deserializedJsonApiObject.getOrders().get(0).getRows().get(0).getItemPriceBrutto());
    assertEquals(auctionId,
        deserializedJsonApiObject.getOrders().get(0).getRows().get(0).getAuctionId());
    assertEquals(ean,
        deserializedJsonApiObject.getOrders().get(0).getRows().get(0).getProductsEan());
    assertEquals(quantity,
        deserializedJsonApiObject.getOrders().get(0).getRows().get(0).getQuantity());
    assertEquals(name, deserializedJsonApiObject.getOrders().get(0).getRows().get(0).getName());
  }

  @ParameterizedTest
  @CsvSource({"ZGI08N"})
  void deserializationOfJsonShouldCheckDeliveryPointId(String deliveryPointId) throws IOException {
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON.getAbsolutePath());

    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);

    //then
    assertTrue(deserializedJsonApiObject.getClass().isAssignableFrom(OrdersBaseLinkerJson.class));
    assertEquals(deliveryPointId,
        deserializedJsonApiObject.getOrders().get(0).getAddressBoxMachine());
  }

  @Test
  void deserializationShouldWriteObjectToFileAndBeTheSameAsInputFile()
      throws IOException, URISyntaxException {
    //given
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON.getAbsolutePath());
    File actual = new File(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON.getAbsolutePath());
    File expected = new File(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON.getAbsolutePath());
    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    serialize.save(new DataOutputStream(new FileOutputStream(actual)), deserializedJsonApiObject,
        FileFormatType.JSON);
    //then
    assertTrue(FileUtils.contentEquals(expected, actual));
  }
}
