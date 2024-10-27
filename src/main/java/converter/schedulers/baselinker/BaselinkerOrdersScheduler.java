package converter.schedulers.baselinker;

import converter.configuration.FILES;
import converter.configuration.FTP_SERVER;
import converter.configuration.ObjectMapperInstance;
import converter.database.MongoDbBaseLinkerOrders;
import converter.exceptions.DatabaseOperationException;
import converter.exceptions.FtpServiceException;
import converter.exceptions.SchedulerOperationException;
import converter.exceptions.ServiceOperationException;
import converter.helper.DateTimeFunctions;
import converter.helper.FileHelper;
import converter.helper.StringProcessor;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerNosql;
import converter.mapper.baselinker.orders.BaseLinkerNoSqlToJson;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.schedulers.orders.SchedulerOrdersImpl;
import converter.schedulers.orders.SchedulersInterfaceOrders;
import converter.serializers.ObjectsDeserializer;
import converter.services.API.baselinker.APIGatewayBaselinker;
import converter.services.API.baselinker.RequestMethod;
import converter.services.API.baselinker.orders.RequestParameters;
import converter.services.DatabaseServiceOrdersImpl;
import converter.services.DatabaseServiceOrdersInterface;
import converter.services.FileTranslation;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Optional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "baselinker.schedulers.orders.enabled", havingValue = "true", matchIfMissing = true)
public class BaselinkerOrdersScheduler {

  private static final Logger log = LoggerFactory.getLogger(BaselinkerOrdersScheduler.class);

  APIGatewayBaselinker apiGatewayBaselinker;
  FileTranslation fileTranslationBaselinker;
  FtpServicePromoOrders ftpServicePromoOrders;
  DatabaseServiceOrdersInterface<OrderBaseLinkerJson> databaseService;
  SchedulersInterfaceOrders schedulersInterfaceOrders;
  MongoDbBaseLinkerOrders mongoDbBaseLinker;
  private boolean isCheckingAllOrdersOnStartupInFtpDone = false;

  public BaselinkerOrdersScheduler(APIGatewayBaselinker apiGatewayBaselinker,
      @Qualifier("BASELINKER-FILETRANSLATION") FileTranslation fileTranslationBaselinker,
      FtpServicePromoOrders ftpServicePromoOrders, MongoDbBaseLinkerOrders mongoDbBaseLinker) {
    this.apiGatewayBaselinker = apiGatewayBaselinker;
    this.fileTranslationBaselinker = fileTranslationBaselinker;
    this.ftpServicePromoOrders = ftpServicePromoOrders;
    this.mongoDbBaseLinker = mongoDbBaseLinker;
    this.databaseService = new DatabaseServiceOrdersImpl(mongoDbBaseLinker,
        new BaseLinkerNoSqlToJson(), new BaseLinkerJsonToBaseLinkerNosql());
    this.schedulersInterfaceOrders = new SchedulerOrdersImpl(apiGatewayBaselinker,
        fileTranslationBaselinker, ftpServicePromoOrders, databaseService);
  }

  private OrdersBaseLinkerJson deserializeResponse(HttpResponse<OrdersBaseLinkerJson> response)
      throws IOException {
    OrdersBaseLinkerJson ordersBaseLinkerJson;
    try (DataInputStream inputStream = new DataInputStream(
        new CharSequenceInputStream((CharSequence) response.body(), "UTF-8"))) {
      ordersBaseLinkerJson = (OrdersBaseLinkerJson) ObjectsDeserializer.load(inputStream,
          OrdersBaseLinkerJson.class);
    }
    return ordersBaseLinkerJson;
  }

  private EnumMap<RequestParameters, String> createEnumMapRequestMapToCallForAllOrdersFromTime(
      Long unixTimestamp) {
    EnumMap<RequestParameters, String> map = new EnumMap<>(RequestParameters.class);
    map.put(RequestParameters.dateFrom, StringProcessor.extractTenDigits(unixTimestamp + 1));
    return map;
  }

  private OrdersBaseLinkerJson obtainRecentOrdersFromApi()
      throws IOException, ServiceOperationException, InterruptedException {
    Long unixTimeStampLastOrder = 1669201003L;

    Optional<OrderBaseLinkerJson> lastOrderInDatabase = Optional.ofNullable(
        databaseService.getLatestOrder());
    if (lastOrderInDatabase.isPresent()
        && lastOrderInDatabase.get().getDateAddGmtUnixTimestamp() != null) {
      unixTimeStampLastOrder = lastOrderInDatabase.get().getDateAddGmtUnixTimestamp();
    } else {
      log.info("No orders in database. Fetching all orders from api to database since datetime: "
          + DateTimeFunctions.convertUnixTimeStampToLocalDateTime(unixTimeStampLastOrder,
          ZoneId.of("Europe/Warsaw")));
    }

    OrdersBaseLinkerJson obtainedOrdersFromApi = deserializeResponse(
        apiGatewayBaselinker.orderQuery(RequestMethod.getOrders,
            createEnumMapRequestMapToCallForAllOrdersFromTime(unixTimeStampLastOrder + 1)));
    return obtainedOrdersFromApi;
  }

  @Scheduled(initialDelay = 100 * 20, fixedDelay = Long.MAX_VALUE)
  public void startup() throws ServiceOperationException {
    loadOrders();
    isCheckingAllOrdersOnStartupInFtpDone = true;
  }

  @Scheduled(cron = "${baselinker.schedulers.ftpCheckScheduler.cron}", zone = "Europe/Warsaw")
  @ConditionalOnProperty(name = "baselinker.schedulers", havingValue = "true", matchIfMissing = true)
  @SchedulerLock(
      name = "ftpCheckTaskBaselinker",
      lockAtLeastFor = "${baselinker.scheduler.lockMinimalTime}",
      lockAtMostFor = "${baselinker.scheduler.lockMaxTime}"
  )
  public void ftpCheck() {
    try {
      schedulersInterfaceOrders.ftpCheck();
    } catch (SchedulerOperationException | IOException e) {
      log.error("Error in ftpCheckTaskBaselinker: {}", e.getMessage());
    }
  }

  @Scheduled(cron = "${baselinker.schedulers.OrdersBaseLinkerGetterTask.cron}", zone = "Europe/Warsaw")
  @SchedulerLock(
      name = "OrdersBaseLinkerGetterTask",
      lockAtLeastFor = "${scheduler.lockMinimalTime}",
      lockAtMostFor = "${scheduler.lockMaxTime}"
  )
  public void loadOrders() throws ServiceOperationException {
    try {
      FileHelper.clearAllDirectory(FILES.OUTPUT_DIRECTORY_BASELINKER);
      OrdersBaseLinkerJson obtainedOrdersFromApi = obtainRecentOrdersFromApi();
      if (Optional.ofNullable(obtainedOrdersFromApi).isEmpty() || obtainedOrdersFromApi.getOrders()
          .isEmpty()) {
        log.debug("No new orders obtained from API exiting scheduler");
        return;
      }
      log.info("New orders found. Processing started");
        fileTranslationBaselinker.executeTask(Collections.singletonList(obtainedOrdersFromApi));
      for (OrderBaseLinkerJson order : obtainedOrdersFromApi.getOrders()) {
        log.info("Order number {} with creation date {} obtained from server", order.getOrderId(),
            DateTimeFunctions.convertUnixTimeStampToLocalDateTime(
                order.getDateAddGmtUnixTimestamp(), ZoneId.of("Europe/Warsaw")));
        databaseService.add(order);
        log.info("Order number {} with creation date {} added to database", order.getOrderId(),
            DateTimeFunctions.convertUnixTimeStampToLocalDateTime(
                order.getDateAddGmtUnixTimestamp(), ZoneId.of("Europe/Warsaw")));
      }
      schedulersInterfaceOrders.ftpSend(FILES.OUTPUT_DIRECTORY_BASELINKER);
    } catch (IOException | DatabaseOperationException | ParseException | ServiceOperationException |
             FtpServiceException | SchedulerOperationException | InterruptedException e) {
      if (e != null && e.getMessage() == null) {
        log.error("Error Fatal: {}", e.getCause());
      } else if (e instanceof ServiceOperationException) {
        log.info("{}", e.getMessage());
      }
    }
  }
}
