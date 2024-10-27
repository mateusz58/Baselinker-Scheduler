package converter.API.baselinker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import converter.configuration.Baselinker.BASELINKER;
import converter.configuration.ObjectMapperInstance;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.stocks.RequestParameters;
import converter.services.API.baselinker.stocks.StocksCalls;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StocksApiTests {

  StocksCalls stocksApi;

  EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);

  @BeforeEach
  void setUp() throws Exception {
    BASELINKER.TOKEN = "3013929-3039265-IZRE4EWHCKR45UW45FM7WIU636J1ZX8S4B9CXCXN80UKZIBWOQ7LMTSQCXMQU2SM";
    BASELINKER.GATEWAY = "https://api.baselinker.com/connector.php";
    stocksApi = new StocksCalls();
    map.put(RequestParameters.inventory_id, "40640");
  }

  @Test
  void getInventoryProductsStockShouldReturn200WithJsonNode()
      throws URISyntaxException, IOException, InterruptedException {
    //given
    int given = 200;
    JsonNode jsonNode = ObjectMapperInstance.getInstanceJson().createObjectNode()
        .put("inventory_id", "40640");
    //when
    int actual = stocksApi.performStockQuery(RequestMethod.getInventoryProductsStock, jsonNode)
        .statusCode();
    //then
    assertEquals(given, actual);
  }

  @Test
  void getInventoryProductsStockShouldReturn200MapEnum()
      throws URISyntaxException, IOException, InterruptedException {
    //given
    int given = 200;
    map.put(RequestParameters.inventory_id, "40640");
    //when
    int actual = stocksApi.performStockQuery(RequestMethod.getInventoryProductsStock, map)
        .statusCode();
    //then
    assertEquals(given, actual);
  }


  @Test
  void getInventoryProductsStockShouldNotReturnExceptionWhenCalledWithJsonNodeParametres() {
    //given
    JsonNode jsonNode = ObjectMapperInstance.getInstanceJson().createObjectNode()
        .put("inventory_id", "40640");

    assertAll(() -> stocksApi.performStockQuery(RequestMethod.getInventoryProductsStock, jsonNode));
  }

  @Test
  void getInventoryProductsStockShouldNotReturnExceptionWhenCalledWithMapParametres() {
    //given
    assertAll(() -> stocksApi.performStockQuery(RequestMethod.getInventoryProductsStock, map));
  }

  @Test
  void updateInventoryStocksShouldReturnMessageStorageWithSpecifiedIdentifierDoesNotExistWhenCalledWithUnkownInventoryAndWarehouse()
      throws IOException, InterruptedException {
    Map<LinkedHashSet<String>, Integer> inventory_stocks = new HashMap<>();
    LinkedHashSet<String> pairEanProductId = new LinkedHashSet<>();
    pairEanProductId.add("4065804320286");
    pairEanProductId.add("188106841");

    String actual = ObjectMapperInstance.getInstanceJson().readTree(
        stocksApi.performStockUpdateQuery("inventory_id_1", "warehouse_id_2", inventory_stocks)
            .body().toString()).get("error_message").toString();
    String expected = "Storage with specified identifier does not exist.";

    assertEquals(expected, actual.replaceAll("\"", ""));
  }

  @Test
  void updateInventoryStocksShoulReturnInfoAboutBadFormatWhenProvidedNonnumericalEAN()
      throws IOException, InterruptedException {
    Map<LinkedHashSet<String>, Integer> inventory_stocks = new HashMap<>();
    LinkedHashSet<String> pairEanProductId = new LinkedHashSet<>();
    pairEanProductId.add("4065804320286");
    pairEanProductId.add("188106841");
    inventory_stocks.put(pairEanProductId, 1);

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockUpdateQuery("40640", "bl_51640", inventory_stocks).body().toString())
        .toString();
    String actual = ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("warnings")
        .toString();
    String expected = "{BAD_EAN:ID produktu BAD_EAN nie jest typu int.}";

    assertEquals(expected.trim(), actual.replaceAll("\"", "").trim());
  }

  @Test
  void updateInventoryStocksShoulReturnWarningaboutNotExistingCatalogWhenProvidingNotExistingEAN()
      throws IOException, InterruptedException {
    Map<LinkedHashSet<String>, Integer> inventory_stocks = new HashMap<>();
    LinkedHashSet<String> pairEanProductId = new LinkedHashSet<>();
    pairEanProductId.add("23423434");
    pairEanProductId.add("77777777777");
    inventory_stocks.put(pairEanProductId, 1);

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockUpdateQuery("40640", "bl_51640", inventory_stocks).body().toString())
        .toString();
    String actual = ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("warnings")
        .toString();
    String expected = "{77777777777:We wskazanym katalogu nie znaleziono produktu o ID 77777777777}";

    assertEquals(expected, actual.replaceAll("\"", ""));
  }

  @Test
  void updateInventoryStocksShoulReturnOkWhenTryingToUpdateEanThatExistInBaselinkerDatabaseInPandaInventory()
      throws IOException, InterruptedException {
    Map<LinkedHashSet<String>, Integer> inventory_stocks = new HashMap<>();
    LinkedHashSet<String> pairEanProductId = new LinkedHashSet<>();
    pairEanProductId.add("4065804320286");
    pairEanProductId.add("188106841");
    inventory_stocks.put(pairEanProductId, 10);

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockUpdateQuery("40640", "bl_51640", inventory_stocks).body().toString())
        .toString();
    String actual = responseJson.toString();
    String expected = "{\"status\":\"SUCCESS\",\"counter\":1,\"warnings\":{}}";
//
    assertEquals(expected, actual);
  }

  @Test
  void updateInventoryStocksShouldReturnOkWhenTryingToUpdateEanThatExistInBaselinkerDatabaseInOffpriceInventory()
      throws IOException, InterruptedException {
    Map<LinkedHashSet<String>, Integer> inventory_stocks = new HashMap<>();
    LinkedHashSet<String> pairEanProductId = new LinkedHashSet<>();
    pairEanProductId.add("4065804054228");
    pairEanProductId.add("161617221");
    inventory_stocks.put(pairEanProductId, 10);

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockUpdateQuery("40541", "bl_51640", inventory_stocks).body().toString())
        .toString();
    String actual = responseJson.toString();
    String expected = "{\"status\":\"SUCCESS\",\"counter\":1,\"warnings\":{}}";
//
    assertEquals(expected, actual);
  }

  @Test
  void getInventoryProductListShouldReturnProductListForPandaInventory()
      throws IOException, InterruptedException {
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.inventory_id, "40640");

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockQuery(RequestMethod.getInventoryProductsList, map).body().toString())
        .toPrettyString();

    String actual = ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("status")
        .toString();
    String expected = "SUCCESS";

    assertEquals(expected, actual.replaceAll("\"", ""));
  }

  @Test
  void getInventoryProductListShouldReturnProductListForOffPriceInventory()
      throws IOException, InterruptedException {
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.inventory_id, "40541");

    String responseJson = ObjectMapperInstance.getInstanceJson().readTree(
            stocksApi.performStockQuery(RequestMethod.getInventoryProductsList, map).body().toString())
        .toPrettyString();

    String actual = ObjectMapperInstance.getInstanceJson().readTree(responseJson).get("status")
        .toString();
    String expected = "SUCCESS";

    assertEquals(expected, actual.replaceAll("\"", ""));
  }
}
