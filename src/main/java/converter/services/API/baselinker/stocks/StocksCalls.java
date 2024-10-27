package converter.services.API.baselinker.stocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import converter.configuration.Baselinker.BASELINKER;
import converter.configuration.ObjectMapperInstance;
import converter.exceptions.baselinker.IncorrectRequestException;
import converter.helper.StringProcessor;
import converter.services.API.baselinker.HttpHeaderMaps;
import converter.services.API.baselinker.RequestMethod;
import converter.services.http.HttpServiceClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class StocksCalls {

  private static final Logger log
      = LoggerFactory.getLogger(StocksCalls.class);
  private final HttpRequest.Builder builderBase;

  public StocksCalls() {
    builderBase = HttpRequest.newBuilder()
        .uri(URI.create(BASELINKER.GATEWAY));
    HttpHeaderMaps.headerMap.forEach((headerKey, headerValues) -> headerValues.forEach(
        value -> builderBase.header(headerKey, value)));
  }

  public StocksCalls(HttpRequest.Builder builderBase) {
    this.builderBase = builderBase.copy();
  }

  public HttpResponse performStockQuery(RequestMethod method, JsonNode parameters)
      throws IOException, InterruptedException {
    HttpRequest.Builder builderCopy = builderBase.copy();
    if (parameters == null) {
      throw new IncorrectRequestException("Query parameters can not be null");
    }
    builderCopy.POST(HttpRequest.BodyPublishers.ofString(
        "method=" + method + "&parameters=" + parameters.toPrettyString()));
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

  public HttpResponse performStockQuery(RequestMethod method,
      EnumMap<RequestParameters, ?> parameters) throws IOException, InterruptedException {
    HttpRequest.Builder builderCopy = builderBase.copy();
    if (parameters == null) {
      throw new IncorrectRequestException("Query parameters can not be null");
    }
    builderCopy.POST(HttpRequest.BodyPublishers.ofString("method=" + method + "&parameters="
        + StringProcessor.convertMapToJson(parameters)));
    HttpResponse response = HttpServiceClient.post(builderCopy.build());
    JsonNode temp = ObjectMapperInstance.getInstanceJson().readTree(response.body().toString());
    if (response.statusCode() != 200) {
      if (response.body().toString().contains("error_message") || response.body().toString()
          .contains("warnings")) {
        if (temp.has("error_message")) {
          log.info("Incorrect request: {}", temp.get("error_message").toString());
        }
        if (temp.has("warnings")) {
          log.info("Incorrect request: {}", temp.get("warnings").toString());
        }
      }
      throw new IncorrectRequestException(response.body().toString());
    } else if (response.statusCode() == 200 && response.body().toString().contains("SUCCESS")) {
      if (response.body().toString().contains("message")) {
        log.info("Api message: {}", temp.get("message").toString());
      }
      return response;
    } else {
      log.info("Incorrect request: {}",
          ObjectMapperInstance.getInstanceJson().readTree(response.body().toString())
              .get("error_message").toString());
      throw new IncorrectRequestException(response.body().toString());
    }
  }

  public HttpResponse performStockUpdateQuery(String inventoryId, String warehouseId,
      Map<LinkedHashSet<String>, Integer> inventoryStocks)
      throws IOException, InterruptedException {
    if (inventoryId == null) {
      log.error("Null value for inventoryId provided");
      throw new IllegalArgumentException("Null value for inventory Id provided");
    }
    if (warehouseId == null) {
      log.error("Null value for warehouseId provided");
      throw new IllegalArgumentException("Null value for warehouseId provided");
    }

    List<Map<LinkedHashSet<String>, Integer>> batches = divideIntoBatches(inventoryStocks);

    HttpResponse response = null;

    for (Map<LinkedHashSet<String>, Integer> batch : batches) {
      ObjectNode rootNode = createRequestJsonNode(inventoryId, warehouseId, batch);
      log.debug("Performing HTTP request to update stocks with body: {}",
          rootNode.toPrettyString());
      HttpRequest.Builder builderCopy = builderBase.copy();
      builderCopy.POST(HttpRequest.BodyPublishers.ofString(
          "method=" + RequestMethod.updateInventoryProductsStock + "&parameters="
              + rootNode.toPrettyString()));
      response = HttpServiceClient.post(builderCopy.build());
      processResponse(response, inventoryId);
    }
    return response;
  }

  private List<Map<LinkedHashSet<String>, Integer>> divideIntoBatches(
      Map<LinkedHashSet<String>, Integer> inventoryStocks) {
    List<Map<LinkedHashSet<String>, Integer>> batches = new ArrayList<>();
    Map<LinkedHashSet<String>, Integer> batch = new HashMap<>();
    int count = 0;
    for (Map.Entry<LinkedHashSet<String>, Integer> entry : inventoryStocks.entrySet()) {
      batch.put(entry.getKey(), entry.getValue());
      count++;
      if (count == 1000) {
        batches.add(batch);
        batch = new HashMap<>();
        count = 0;
      }
    }
    if (count > 0) {
      batches.add(batch);
    }
    return batches;
  }

  private ObjectNode createRequestJsonNode(String inventoryId, String warehouseId,
      Map<LinkedHashSet<String>, Integer> inventoryStocks) {
    ObjectNode rootNode = ObjectMapperInstance.getInstanceJson().createObjectNode();
    rootNode.put("inventory_id", inventoryId);
    ObjectNode productsNode = rootNode.putObject("products");
    for (Map.Entry<LinkedHashSet<String>, Integer> entry : inventoryStocks.entrySet()) {
      String arr[] = entry.getKey().toArray(new String[entry.getKey().size()]);
      log.debug(
          "Attempting to update stock of productId {} EAN {} to {} in inventory Id {}. Total number of products {} to update VIA API",
          arr[1], arr[0], entry.getValue(), inventoryId, inventoryStocks.size());
      ObjectNode productNode = productsNode.putObject(arr[1]);
      productNode.put(warehouseId, entry.getValue());
    }
    return rootNode;
  }

  private void processResponse(HttpResponse response, String inventoryId) throws IOException {
    if (response.statusCode() != 200) {
      if (response.body().toString().contains("error_message") || response.body().toString()
          .contains("warnings") || response.body().toString().contains("message")) {
        JsonNode temp = ObjectMapperInstance.getInstanceJson().readTree(response.body().toString());
        if (temp.has("error_message")) {
          log.info("Incorrect request: {}", temp.get("error_message").toString());
        }
        if (temp.has("warnings")) {
          log.info("Incorrect request: {}", temp.get("warnings").toString());
        }
        if (temp.has("message")) {
          log.info("Incorrect request: {}", temp.get("message").toString());
        }
      }
    } else if (response.statusCode() == 200 && response.body().toString().contains("SUCCESS")) {
      String responseJson = ObjectMapperInstance.getInstanceJson()
          .readTree(response.body().toString()).toPrettyString();
      log.debug(response.body().toString());
      if (responseJson.contains("warnings") && !responseJson.contains("counter")) {
        log.info("Unsuccessfully updated stock for inventory {} Api message: {}", inventoryId,
            ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("warnings")
                .toString());
      } else if (responseJson.contains("message")) {
        log.info("Unsuccessfully updated stock for inventory {} Api message: {}", inventoryId,
            ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("message")
                .toString());
      } else if (responseJson.contains("counter")) {
        log.info(
            "Successfully updated stock for inventory {} Number of products updated in total {} Api message: {}",
            inventoryId,
            ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("counter")
                .toString(),
            ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("warnings")
                .toString());
      }
    } else {
      String responseJson = ObjectMapperInstance.getInstanceJson()
          .readTree(response.body().toString()).toPrettyString();
      log.info("Unsuccessfully updated stock inventory {} Api message {}", inventoryId,
          ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("error_message")
              .toString());
    }
  }
}
