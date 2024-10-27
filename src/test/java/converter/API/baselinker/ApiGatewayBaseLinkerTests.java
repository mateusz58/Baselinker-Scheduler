package converter.API.baselinker;

import static org.junit.jupiter.api.Assertions.assertAll;

import converter.configuration.Baselinker.BASELINKER;
import converter.services.API.baselinker.APIGatewayBaselinker;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.RequestParameters;
import java.net.URISyntaxException;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiGatewayBaseLinkerTests {

  APIGatewayBaselinker apiGatewayBaselinker;

  @BeforeEach
  public void setUp() throws URISyntaxException {
    BASELINKER.TOKEN = "3013929-3039265-IZRE4EWHCKR45UW45FM7WIU636J1ZX8S4B9CXCXN80UKZIBWOQ7LMTSQCXMQU2SM";
    BASELINKER.GATEWAY = "https://api.baselinker.com/connector.php";
    apiGatewayBaselinker = new APIGatewayBaselinker();
  }

  @Test
  public void getOrdersWithMapArgumentShouldExecuteWithoutErrors() {
    EnumMap map = new EnumMap<RequestParameters, String>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, 10000000);
    assertAll(() -> apiGatewayBaselinker.orderQuery(RequestMethod.getOrders, map));
  }
}
