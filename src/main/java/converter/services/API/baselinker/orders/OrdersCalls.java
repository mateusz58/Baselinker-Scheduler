package converter.services.API.baselinker.orders;

import converter.configuration.Baselinker.BASELINKER;
import converter.exceptions.baselinker.IncorrectRequestException;
import converter.helper.StringProcessor;
import converter.services.API.baselinker.HttpHeaderMaps;
import converter.services.API.baselinker.RequestMethod;
import converter.services.http.HttpServiceClient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class OrdersCalls {

  private static final Logger log
      = LoggerFactory.getLogger(OrdersCalls.class);
  private final HttpRequest.Builder builderBase;

  public OrdersCalls() throws URISyntaxException {
    builderBase = HttpRequest.newBuilder()
        .uri(new URI(BASELINKER.GATEWAY));
    HttpHeaderMaps.headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> builderBase.header(headerKey, value)));
    builderBase.POST(HttpRequest.BodyPublishers.ofString("method=" + RequestMethod.getOrders));
  }

  public OrdersCalls(HttpRequest.Builder builderBase) {
    this.builderBase = builderBase.copy()
        .POST(HttpRequest.BodyPublishers.ofString("method=" + RequestMethod.getOrders));
  }

  public HttpResponse performOrderQuery(RequestMethod method,
      EnumMap<RequestParameters, ?> parameters) throws IOException, InterruptedException {
    HttpRequest.Builder builderCopy = builderBase.copy();
    if (parameters == null) {
      throw new IncorrectRequestException("Query parameters can not be null");
    }
    builderCopy.POST(HttpRequest.BodyPublishers.ofString("method=" + method + "&parameters="
        + StringProcessor.convertMapToJson(parameters)));
    HttpResponse response = HttpServiceClient.post(builderCopy.build());
    if (response.statusCode() != 200) {
      throw new IncorrectRequestException(response.body().toString());
    }
    if (response.statusCode() == 200 && response.body().toString().contains("SUCCESS")) {
      return response;
    } else {
      throw new IncorrectRequestException(response.body().toString());
    }
  }

  public HttpResponse performOrderQuery(RequestMethod method)
      throws IOException, InterruptedException {
    HttpRequest.Builder builderCopy = builderBase.copy();
    builderCopy.POST(HttpRequest.BodyPublishers.ofString("method=" + method));
    HttpResponse<?> response = HttpServiceClient.post(builderCopy.build());
    if (response.statusCode() != 200) {
      throw new IncorrectRequestException(response.body().toString());
    }
    if (response.statusCode() == 200 && response.body().toString().contains("SUCCESS")) {
      return response;
    } else {
      throw new IncorrectRequestException(response.body().toString());
    }
  }
}
