package converter.timeZoneTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.configuration.FileFormatType;
import converter.helper.StringProcessor;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.serializers.ObjectsDeserializer;
import converter.services.API.baselinker.APIGatewayBaselinker;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.RequestParameters;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BaselinkerTimeZonesUnitTestsHttp {

  APIGatewayBaselinker apiGateway;
  ObjectsDeserializer objectsDeserializer;

  @BeforeEach
  void setUp() throws Exception {
    apiGateway = new APIGatewayBaselinker();
  }

  @Test
  void shouldReturnBaseLinkerOrderBasedOnDateFromParametreThenCheckSerializeToXmlBaseLinkerFileAndChangeZoneOfDateTimeFields()
      throws IOException, InterruptedException, URISyntaxException {
    //given
    Long given = 1669201003L; // 23.11.2022 10:56 London Time
    Long expected = 1669204603L; // 23.11.2022 11:56:43 Warsaw Time
    Long actual;
    //when
    // obtain data from api
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, StringProcessor.extractTenDigits(given));
    HttpResponse response = apiGateway.orderQuery(RequestMethod.getOrders, map);
    OrdersBaseLinkerJson orders = (OrdersBaseLinkerJson) objectsDeserializer.load(
        new DataInputStream(new CharSequenceInputStream((CharSequence) response.body(), "UTF-8")),
        OrdersBaseLinkerJson.class, FileFormatType.JSON);

    // translate and save as xml

    //then check if order with passed parametre is returned
    assertTrue(orders.getOrders().stream().map(s -> s.getDateAddGmtUnixTimestamp())
        .anyMatch(s -> s.equals(given)));
  }
}
