package converter.API.baselinker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import converter.configuration.Baselinker.BASELINKER;
import converter.exceptions.baselinker.IncorrectRequestException;
import converter.helper.StringProcessor;
import converter.services.API.baselinker.APIGatewayBaselinker;
import converter.services.API.baselinker.HeaderKeys;
import converter.services.API.baselinker.HeaderValues;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.RequestParameters;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiGatewayBaseLinkerTestsExceptionsOnly {

  APIGatewayBaselinker apiGatewayBaselinker;
  HttpRequest.Builder request;
  private Map<String, List<String>> headerMap = new HashMap<>();

  private EnumMap<RequestParameters, String> createEnumMapRequestMap(Long unixTimestamp) {
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, StringProcessor.extractTenDigits(unixTimestamp + 1));
    return map;
  }

  @BeforeEach
  public void setUp() throws URISyntaxException {
    BASELINKER.TOKEN = "3013929-3039265-IZRE4EWHCKR45UW45FM7WIU636J1ZX8S4B9CXCXN80UKZIBWOQ7LMTSQCXMQU2SM";
    BASELINKER.GATEWAY = "https://api.baselinker.com/connector.php";
    headerMap.put(HeaderKeys.ContentType.toString(),
        Collections.singletonList(HeaderValues.URLEncoded.toString()));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList(BASELINKER.TOKEN));
    request = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> request.header(headerKey, value)));
    apiGatewayBaselinker = new APIGatewayBaselinker(request);
  }

  @Test
  public void getOrdersShouldExecuteWithoutErrors() {
    assertAll(() -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders));
  }

  @Test
  public void getOrdersWithMapArgumentShouldExecuteWithoutErrors() {
    EnumMap map = new EnumMap<RequestParameters, String>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, 10000000);
    assertAll(() -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
  }

  @Test
  void getOrdersFunctionShouldReturnMessageEmptyTokenWhenTokenIsNotProvided()
      throws URISyntaxException {
    //given
    headerMap.put(HeaderKeys.ContentType.toString(),
        Collections.singletonList(HeaderValues.URLEncoded.toString()));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList(""));
    request = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> request.header(headerKey, value)));
    apiGatewayBaselinker = new APIGatewayBaselinker(request);
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.orderId, "435345345345");
    //then
    Throwable throwable = assertThrows(IncorrectRequestException.class,
        () -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
    assertEquals("ERROR_EMPTY_TOKEN", throwable.getMessage());
  }

  @Test
  void getOrdersFunctionShouldReturnIncorrectTokenMessageWhenTokenIsInCorrect()
      throws URISyntaxException {
    //given
    headerMap.put(HeaderKeys.ContentType.toString(),
        Collections.singletonList(HeaderValues.URLEncoded.toString()));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList("BAD TOKEN"));
    request = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> request.header(headerKey, value)));
    apiGatewayBaselinker = new APIGatewayBaselinker(request);
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.orderId, "435345345345");
    //then
    Throwable throwable = assertThrows(IncorrectRequestException.class,
        () -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
    assertEquals("ERROR_BAD_TOKEN", throwable.getMessage());
  }

  @Test
  void getOrdersFunctionShouldReturnErrorUnkownMethodWhenContentTypeIsIncorrect()
      throws URISyntaxException {
    //given
    headerMap.put(HeaderKeys.ContentType.toString(), Collections.singletonList("sdasdsadads"));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList(BASELINKER.TOKEN));
    request = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> request.header(headerKey, value)));
    apiGatewayBaselinker = new APIGatewayBaselinker(request);
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.orderId, "435345345345");
    //then
    Throwable throwable = assertThrows(IncorrectRequestException.class,
        () -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
    assertEquals("ERROR_UNKNOWN_METHOD", throwable.getMessage());
  }

  @Test
  void getOrdersFunctionShouldThrowConnectExceptionWhenThereIsNoServerOrInternetConnection()
      throws URISyntaxException {
    //given
    headerMap.put(HeaderKeys.ContentType.toString(), Collections.singletonList("sdasdsadads"));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList(BASELINKER.TOKEN));
    request = HttpRequest.newBuilder()
        .uri(new URI("https://api.baselinker1.com/connector.php"));
    headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> request.header(headerKey, value)));
    apiGatewayBaselinker = new APIGatewayBaselinker(request);
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.orderId, "435345345345");
    //then
    Throwable throwable = assertThrows(ConnectException.class,
        () -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
  }
}
