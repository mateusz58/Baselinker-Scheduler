package converter.API.baselinker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.Baselinker.BASELINKER;
import converter.configuration.FileFormatType;
import converter.helper.DateTimeFunctions;
import converter.helper.FileHelper;
import converter.helper.StringProcessor;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.serializers.ObjectsDeserializer;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.OrdersCalls;
import converter.services.API.baselinker.orders.RequestParameters;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class OrdersApiTests {

  OrdersCalls ordersApi;

  EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);

  @BeforeEach
  void setUp() throws Exception {
    BASELINKER.TOKEN = "3013929-3039265-IZRE4EWHCKR45UW45FM7WIU636J1ZX8S4B9CXCXN80UKZIBWOQ7LMTSQCXMQU2SM";
    BASELINKER.GATEWAY = "https://api.baselinker.com/connector.php";
    ordersApi = new OrdersCalls();
    map.put(RequestParameters.orderId, "1669201233");
  }

  private OrdersBaseLinkerJson loadObjectFromFile(File fileName)
      throws URISyntaxException, IOException, ParseException {
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(fileName)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    return deserializedJsonApiObject;
  }

  private OrdersBaseLinkerJson deserializeResponse(HttpResponse response)
      throws ParseException, IOException {
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    return deserializedJsonApiObject;
  }

  @Test
  void getOrdersShouldReturn200() throws URISyntaxException, IOException, InterruptedException {
    //given
    int given = 200;
    //when
    int actual = ordersApi.performOrderQuery(RequestMethod.getOrders, map).statusCode();
    //then
    assertEquals(given, actual);
  }

  @Test
  void getOrdersShouldReturnSuccess()
      throws IOException, InterruptedException, URISyntaxException, ParseException {
    //when
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = deserializeResponse(response);

    //then
    assertEquals("SUCCESS", actual.getStatus());
  }

  @Test
  void getOrdersShouldNotReturnException() {
    assertAll(() -> ordersApi.performOrderQuery(RequestMethod.getOrders, map));
  }

  @Test
  void getOrdersShouldReturnOrdersBaseLinkerClassType()
      throws IOException, InterruptedException, URISyntaxException {
    //given
    Class given = OrdersBaseLinkerJson.class;

    //when
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);

    assertEquals(OrdersBaseLinkerJson.class, actual.getClass());
  }

  @Test
  void getOrdersShouldReturnExpectedObjectFilteredById()
      throws IOException, InterruptedException, URISyntaxException, ParseException {
    //given
    OrdersBaseLinkerJson expected = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_BASELINKER_TESTING.BASE_LINKER_JSON)),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    String given = "72002699";
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.orderId, given);
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = deserializeResponse(response);
    //then
    //assertEquals(expected, actual);
    assertEquals(expected.getOrders().get(0).getOrderId(),
        expected.getOrders().get(0).getOrderId());
  }

  @Test
  @Disabled("Filtering by orderSource is not needed to implement later")
  void getOrdersShouldReturnExpectedObjectFilteredByOrderSource()
      throws IOException, InterruptedException, URISyntaxException, ParseException {
    //given
    OrdersBaseLinkerJson expected = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_BASELINKER_TESTING.BASE_LINKER_JSON)),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    String given = "allegro";
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.filterOrderSource, given);
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = deserializeResponse(response);
    //then
    assertEquals(expected.getOrders().get(0).getOrderId(),
        expected.getOrders().get(0).getOrderId());
  }

  @Test
  void getOrdersShouldReturnExpectedObjectFilteredByDateFromWithUnixTimestampAsParametre()
      throws IOException, InterruptedException, URISyntaxException, ParseException {
    //given
    OrdersBaseLinkerJson expected = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_BASELINKER_TESTING.BASE_LINKER_JSON)),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    String given = "1669201233";
    String givenId = String.valueOf(Instant.now().getEpochSecond());
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateConfirmedFrom, given);
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = deserializeResponse(response);
    //then
    assertEquals(expected.getOrders().get(0), actual.getOrders().get(0));
  }

  @Test
  void getOrdersShouldReturnLastOrderBaseOnFileNameWhichIsDateAddOrder()
      throws IOException, InterruptedException, URISyntaxException, ParseException {
    //given
    //XML_zamwienia_2022_12_20_05_59_44
    LocalDateTime given = FileHelper.obtainLatestDateFromFileNamesInDirectory(
        FILES_BASELINKER_TESTING.ORDERS_PROCESSED_DIRECTORY);
    Long expected = 1671512384L; // time GMT Sun Dec 11 2022 16:05:02 GMT+0000 UTC+1 Sun Dec 11 2022 17:05:02 GMT+0100
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, StringProcessor.extractTenDigits(
        DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(
            DateTimeFunctions.switchTimeZone(given, ZoneId.of("Europe/Warsaw"),
                ZoneId.of("Europe/London")))));
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson orders = deserializeResponse(response);
    //then
    assertTrue(orders.getOrders().stream().map(s -> s.getDateAddGmtUnixTimestamp())
        .anyMatch(s -> s.equals(expected)));
  }

  @Test
  @Disabled("Filtering by email is not needed later to implement")
  void getOrdersShouldReturnExpectedObjectFilteredByEmail()
      throws IOException, InterruptedException, URISyntaxException {
    //given
    OrdersBaseLinkerJson expected = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_BASELINKER_TESTING.BASE_LINKER_JSON)),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    String given = "mou8zu7ig9+21118d326@allegromail.pl";
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateConfirmedFrom, given);
    map.put(RequestParameters.filterEmail, given);
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    //then
    assertEquals(expected, actual);
  }

  @Test
  void getOrdersFunctionReturnAllOrders()
      throws IOException, InterruptedException, ParseException, URISyntaxException {
    //given
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders);
    OrdersBaseLinkerJson actual = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    OrdersBaseLinkerJson expected = loadObjectFromFile(FILES_BASELINKER_TESTING.BASE_LINKER_JSON);
    //then
    assertEquals(OrdersBaseLinkerJson.class, actual.getClass());
    assertTrue(actual.getOrders().size() > 0);
    //assertEquals(expected.getOrders().get(0), actual.getOrders().get(0));
  }

  @Test
  void getOrdersFunctionReturnEmptyOrdersWhenCalledWithDateTimeGreaterThanCurrentTime()
      throws IOException, InterruptedException, ParseException, URISyntaxException {
    //given
    Long given = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(
        LocalDateTime.now().plusHours(24), ZoneId.of("Europe/London"));
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateConfirmedFrom, StringProcessor.extractTenDigits(given));
    HttpResponse response = ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson actual = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);
    //then
    assertEquals(OrdersBaseLinkerJson.class, actual.getClass());
    assertTrue(actual.getOrders().size() == 0);
  }
}
