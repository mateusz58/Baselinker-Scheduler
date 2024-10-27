package converter.API.baselinker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import converter.configuration.Baselinker.BASELINKER;
import converter.exceptions.baselinker.IncorrectRequestException;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.OrdersCalls;
import converter.services.API.baselinker.orders.RequestParameters;
import java.net.URISyntaxException;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrdersApiTestsExceptionsOnly {

  OrdersCalls ordersApi;


  @BeforeEach
  void setUp() throws URISyntaxException {
    BASELINKER.TOKEN = "3013929-3039265-IZRE4EWHCKR45UW45FM7WIU636J1ZX8S4B9CXCXN80UKZIBWOQ7LMTSQCXMQU2SM";
    BASELINKER.GATEWAY = "https://api.baselinker.com/connector.php";
    ordersApi = new OrdersCalls();
  }

  @Test
  void httpPostFunctionShouldNotReturnException() {
    assertAll(() -> ordersApi.performOrderQuery(RequestMethod.getOrders));
  }

  @Test
  void getOrdersFunctionShouldReturnIllegalArgumentExceptionWhenParameterQueryisNull() {
    //when
    EnumMap<RequestParameters, String> map = null;
    assertThrows(IncorrectRequestException.class, () -> {
      ordersApi.performOrderQuery(RequestMethod.getOrders, map);
    });
  }

  @Test
  void getOrdersFunctionShouldThrowIncorrectRequestExceptionWhenQueryIsIncorrect() {
    //when
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, "jinsdjofhsfdsdfoi");
    //then
    Throwable throwable = assertThrows(IncorrectRequestException.class,
        () -> ordersApi.performOrderQuery(RequestMethod.getOrders, map));
    assertEquals("ERROR_PARSE_JSON_PARAMETERS", throwable.getMessage());
  }
}
