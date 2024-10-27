package converter.schedulers.baselinker;

import converter.configuration.FILES;
import converter.database.DatabaseStocks;
import converter.exceptions.DatabaseOperationException;
import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import converter.helper.CsvProcessing;
import converter.helper.FileHelper;
import converter.mapper.baselinker.stocks.BaseLinkerJsonToBaseLinkerNosqlStocks;
import converter.model.baseLinkerModel.JSON.stocks.ProductJson;
import converter.model.baseLinkerModel.JSON.stocks.ProductsJson;
import converter.serializers.ObjectsDeserializer;
import converter.services.API.baselinker.APIGatewayBaselinker;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.stocks.RequestParameters;
import converter.services.DatabaseStocksService;
import converter.services.StocksServiceInterface;
import converter.services.ftp.FtpServicePromoStocks;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "baselinker.schedulers.stocks.enabled", havingValue = "true", matchIfMissing = true)
public class BaselinkerStocksScheduler {

  private static final Logger log = LoggerFactory.getLogger(BaselinkerStocksScheduler.class);
  APIGatewayBaselinker apiGatewayBaselinker;
  StocksServiceInterface databaseService;
  DatabaseStocks mongoDbBaselinkerStocks;
  FtpServicePromoStocks ftpServicePromoStocks;
  @Value("${baselinker.inventory.faina-offprice}")
  private String INVENTORY_FAINA_OFFPRICE;
  @Value("${baselinker.inventory.faina-panda}")
  private String INVENTORY_PANDA;
  @Value("${baselinker.warehouse.id}")
  private String WAREHOUSE_ID;


  public BaselinkerStocksScheduler(APIGatewayBaselinker apiGatewayBaselinker,
      FtpServicePromoStocks ftpServicePromoStocks, DatabaseStocks mongoDbBaselinkerStocks) {
    this.apiGatewayBaselinker = apiGatewayBaselinker;
    this.ftpServicePromoStocks = ftpServicePromoStocks;
    this.mongoDbBaselinkerStocks = mongoDbBaselinkerStocks;
    this.databaseService = new DatabaseStocksService(mongoDbBaselinkerStocks,
        new BaseLinkerJsonToBaseLinkerNosqlStocks());
  }

  private ProductsJson deserializeResponse(HttpResponse<ProductsJson> response) throws IOException {
    ProductsJson productsJson;
    try (DataInputStream inputStream = new DataInputStream(
        new CharSequenceInputStream((CharSequence) response.body(), "UTF-8"))) {
      productsJson = (ProductsJson) ObjectsDeserializer.load(inputStream, ProductsJson.class);
    }
    return productsJson;
  }

  @Scheduled(initialDelay = 100 * 20, fixedDelay = Long.MAX_VALUE)
  public void startup()
      throws ServiceOperationException, IOException {
    log.debug("Cleaning folder directory on startup");
    FileHelper.clearAllDirectory(FILES.STOCK_DIRECTORY);
    //stocksDiffLoader();
  }

  private void loadInventoryToDatabase(String inventoryId, ProductsJson productsJson)
      throws DatabaseOperationException, ParseException, ServiceOperationException {
    databaseService.loadInventory(inventoryId, productsJson);
  }

  private ProductsJson loadInventoryFromApi(String inventoryId)
      throws DatabaseOperationException, ParseException, ServiceOperationException, IOException, InterruptedException {
    boolean isProductListEmpty = false;
    Map<String, ProductJson> appendedProductList = new HashMap<>();
    int page = 1;
    while (!isProductListEmpty) {
      EnumMap<RequestParameters, String> mapRequest = new EnumMap<>(RequestParameters.class);
      mapRequest.put(RequestParameters.inventory_id, inventoryId);
      mapRequest.put(RequestParameters.page, String.valueOf(page++));
      Optional<ProductsJson> inventoryFromApi = Optional.ofNullable(deserializeResponse(
          apiGatewayBaselinker.stockQuery(RequestMethod.getInventoryProductsList, mapRequest)));
      if (inventoryFromApi.isPresent()) {
        if (inventoryFromApi.get().getProducts() == null || inventoryFromApi.get().getProducts()
            .isEmpty()) {
          isProductListEmpty = true;
        } else {
          appendedProductList.putAll(inventoryFromApi.get().getProducts());
        }
      } else {
        break;
      }
    }
    return ProductsJson.builder().products(appendedProductList).build();
  }

  @Scheduled(cron = "${baselinker.schedulers.inventory.cron}", zone = "Europe/Warsaw")
  @SchedulerLock(name = "inventoryLoader", lockAtLeastFor = "${baselinker.scheduler.lockMinimalTime.INVENTORY}", lockAtMostFor = "${scheduler.lockMaxTime.INVENTORY}")
  public void inventoryLoader() {
    try {
      log.debug("Attempting to load inventory from faina offprice");
      ProductsJson loadedInventoryFainaOffPrice = loadInventoryFromApi(INVENTORY_FAINA_OFFPRICE);
      loadInventoryToDatabase(INVENTORY_FAINA_OFFPRICE, loadedInventoryFainaOffPrice);
      log.info("Successfully loaded faina offprice inventory");
      log.debug("Attempting to load inventory from panda");
      ProductsJson loadedInventoryPanda = loadInventoryFromApi(INVENTORY_PANDA);
      loadInventoryToDatabase(INVENTORY_PANDA, loadedInventoryPanda);
      log.info("Successfully loaded panda inventory");
    } catch (IOException | ServiceOperationException | InterruptedException |
             DatabaseOperationException | ParseException e) {
      if (e != null && e.getMessage() == null) {
        log.error("Error Fatal: {}", e.getCause());
      } else if (e instanceof ServiceOperationException) {
        log.info("{}", e.getMessage());
      }
    }
  }

  @Scheduled(cron = "${baselinker.schedulers.stocksDiffGetter.cron}", zone = "Europe/Warsaw")
  @SchedulerLock(name = "diffStocksGetterTask", lockAtLeastFor = "${scheduler.lockMinimalTime.STOCKS}", lockAtMostFor = "${scheduler.lockMaxTime.STOCKS}")
  public void stocksDiffLoader() throws ServiceOperationException {
    try {
      log.debug("Attempting to load diff stock");
      Map<String, Integer> inventoryStocksDiffCsvPromo = new HashMap<>();
      ftpServicePromoStocks.getStocks("bestand_diff", "stock_diff.csv", Duration.ofHours(2L));
      CsvProcessing.loadCSVToHashmap(
          Paths.get(FILES.STOCK_DIRECTORY, "stock_diff.csv").toString(),
          inventoryStocksDiffCsvPromo,
          (key, value) -> key, Integer::parseInt);
      Map<LinkedHashSet<String>, Integer> mapProductIdStocksForFainaOffPriceInventory = databaseService.updateStocksInDatabaseReturnHashmapProductIdAndStocks(
          INVENTORY_FAINA_OFFPRICE, inventoryStocksDiffCsvPromo);
      Map<LinkedHashSet<String>, Integer> mapProductIdStocksForPandaInventory = databaseService.updateStocksInDatabaseReturnHashmapProductIdAndStocks(
          INVENTORY_PANDA, inventoryStocksDiffCsvPromo);
      if (mapProductIdStocksForFainaOffPriceInventory == null
          || mapProductIdStocksForFainaOffPriceInventory.isEmpty()) {
        log.info("No changes in stocks for faina offprice inventory");
      } else {
        apiGatewayBaselinker.stockUpdateQuery(INVENTORY_FAINA_OFFPRICE, WAREHOUSE_ID,
            mapProductIdStocksForFainaOffPriceInventory);
      }
      if (mapProductIdStocksForPandaInventory == null
          || mapProductIdStocksForPandaInventory.isEmpty()) {
        log.info("No changes in stocks for panda inventory");
      } else {
        apiGatewayBaselinker.stockUpdateQuery(INVENTORY_PANDA, WAREHOUSE_ID,
            mapProductIdStocksForPandaInventory);
      }
      log.info("Diff stock info loaded");
    } catch (IOException | FtpServiceException
             | InterruptedException | DatabaseOperationException | ParseException e) {
      if (e != null && e.getMessage() == null) {
        log.error("Error Fatal: {}", e.getCause());
      } else if (e instanceof ServiceOperationException) {
        log.info("{}", e.getMessage());
      }
    }
  }

  @Scheduled(cron = "${baselinker.schedulers.stocksFullGetter.cron}", zone = "Europe/Warsaw")
  @SchedulerLock(name = "diffFullGetterTask", lockAtLeastFor = "${scheduler.lockMinimalTime.STOCKS}", lockAtMostFor = "${scheduler.lockMaxTime.STOCKS}")
  public void stocksCompleteLoader() throws ServiceOperationException {
    try {
      log.debug("Attempting to load complete stock");
      Map<String, Integer> inventoryStocksDiffCsvPromo = new HashMap<>();
      ftpServicePromoStocks.getStocks("bestand_komplett", "stock_complete.csv",
          Duration.ofHours(24L));
      CsvProcessing.loadCSVToHashmap(
          Paths.get(FILES.STOCK_DIRECTORY, "stock_complete.csv").toString(),
          inventoryStocksDiffCsvPromo,
          (key, value) -> key, Integer::parseInt);
      Map<LinkedHashSet<String>, Integer> mapProductIdStocksForFainaOffPriceInventory = databaseService.updateStocksInDatabaseReturnHashmapProductIdAndStocks(
          INVENTORY_FAINA_OFFPRICE, inventoryStocksDiffCsvPromo);
      Map<LinkedHashSet<String>, Integer> mapProductIdStocksForPandaInventory = databaseService.updateStocksInDatabaseReturnHashmapProductIdAndStocks(
          INVENTORY_PANDA, inventoryStocksDiffCsvPromo);
      if (mapProductIdStocksForFainaOffPriceInventory == null
          || mapProductIdStocksForFainaOffPriceInventory.isEmpty()) {
        log.info("No changes in stocks for faina offprice inventory");
      } else {
        apiGatewayBaselinker.stockUpdateQuery(INVENTORY_FAINA_OFFPRICE, WAREHOUSE_ID,
            mapProductIdStocksForFainaOffPriceInventory);
      }
      if (mapProductIdStocksForPandaInventory == null
          || mapProductIdStocksForPandaInventory.isEmpty()) {
        log.info("No changes in stocks for panda inventory");
      } else {
        apiGatewayBaselinker.stockUpdateQuery(INVENTORY_PANDA, WAREHOUSE_ID,
            mapProductIdStocksForPandaInventory);
      }
      log.info("Full stock info loaded");
    } catch (IOException | FtpServiceException
             | InterruptedException | DatabaseOperationException | ParseException e) {
      if (e != null && e.getMessage() == null) {
        log.error("Error Fatal: {}", e.getCause());
      } else if (e instanceof ServiceOperationException) {
        log.info("{}", e.getMessage());
      }
    }
  }
}
