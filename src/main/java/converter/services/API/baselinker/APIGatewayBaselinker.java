package converter.services.API.baselinker;

import com.fasterxml.jackson.databind.JsonNode;
import converter.configuration.Baselinker.BASELINKER;
import converter.services.API.ApiInterface;
import converter.services.API.baselinker.orders.OrdersCalls;
import converter.services.API.baselinker.stocks.StocksCalls;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class APIGatewayBaselinker implements ApiInterface {

  public static final Map<String, List<String>> headerMap = new HashMap<>();
  private final HttpRequest.Builder builderBase;
  private final OrdersCalls ordersApi;
  private final StocksCalls stocksApi;

  @Autowired
  public APIGatewayBaselinker() throws URISyntaxException {
    builderBase = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    HttpHeaderMaps.headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> builderBase.header(headerKey, value)));
    ordersApi = new OrdersCalls(builderBase);
    stocksApi = new StocksCalls(builderBase);
  }

  public APIGatewayBaselinker(HttpRequest.Builder request) {
    this.builderBase = request;
    ordersApi = new OrdersCalls(builderBase);
    stocksApi = new StocksCalls(builderBase);
  }

  public HttpResponse orderQuery(RequestMethod method) throws IOException, InterruptedException {
    return ordersApi.performOrderQuery(method);
  }

  public HttpResponse orderQuery(RequestMethod method,
      EnumMap<converter.services.API.baselinker.orders.RequestParameters, ?> params)
      throws IOException, InterruptedException {
    if (params == null) {
      throw new IllegalArgumentException("Null parameter passed to query");
    }
    return ordersApi.performOrderQuery(method, params);
  }

  public HttpResponse stockQuery(RequestMethod method, JsonNode parametres)
      throws IOException, InterruptedException {
    return stocksApi.performStockQuery(method, parametres);
  }

  public HttpResponse stockQuery(RequestMethod method,
      EnumMap<converter.services.API.baselinker.stocks.RequestParameters, ?> params)
      throws IOException, InterruptedException {
    if (params == null) {
      throw new IllegalArgumentException("Null parameter passed to query");
    }
    return stocksApi.performStockQuery(method, params);
  }

  public HttpResponse stockUpdateQuery(String inventory_id, String warehouse_id,
      Map<LinkedHashSet<String>, Integer> stocksIventory) throws IOException, InterruptedException {
    return stocksApi.performStockUpdateQuery(inventory_id, warehouse_id, stocksIventory);
  }
}
