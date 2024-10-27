package converter.schedulers.orders;

import converter.exceptions.FtpServiceException;
import converter.exceptions.SchedulerOperationException;
import converter.exceptions.ServiceOperationException;
import converter.helper.FileHelper;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.schedulers.SchedulerHelper;
import converter.services.API.ApiInterface;
import converter.services.DatabaseServiceOrdersInterface;
import converter.services.FileTranslation;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerOrdersImpl<X> implements SchedulersInterfaceOrders {

  private static Logger log = LoggerFactory.getLogger(SchedulerOrdersImpl.class);
  ApiInterface apigateway;
  FileTranslation fileTranslation;
  FtpServicePromoOrders ftpServicePromoOrders;
  DatabaseServiceOrdersInterface<X> databaseService;

  public SchedulerOrdersImpl(ApiInterface apigateway, FileTranslation fileTranslationBaselinker,
      FtpServicePromoOrders ftpServicePromoOrders,
      DatabaseServiceOrdersInterface<X> databaseService) {
    this.apigateway = apigateway;
    this.fileTranslation = fileTranslationBaselinker;
    this.ftpServicePromoOrders = ftpServicePromoOrders;
    this.databaseService = databaseService;
  }

  public SchedulerOrdersImpl(DatabaseServiceOrdersInterface<X> databaseService,
      FileTranslation fileTranslation, FtpServicePromoOrders ftpServicePromoOrders) {
    this.databaseService = databaseService;
    this.fileTranslation = fileTranslation;
    this.ftpServicePromoOrders = ftpServicePromoOrders;
  }

  @Override
  public void ftpCheck() throws SchedulerOperationException, IOException {
    Path outputPath = null;
    try {
      List falseFtpStatusOrdersJsons = Optional.ofNullable(
          databaseService.findAllOrdersWhereFtpStatusIsFalse()).orElse(Collections.emptyList());
      if (falseFtpStatusOrdersJsons.isEmpty()) {
        log.debug("No Records with false ftp status found. Exiting scheduler");
        return;
      }
      outputPath = SchedulerHelper.createLocalDateTimeFolderName();
      if (falseFtpStatusOrdersJsons.get(0) instanceof OrderBaseLinkerJson) {
        fileTranslation.executeTask(Collections.singletonList(OrdersBaseLinkerJson.builder()
            .orders((List<OrderBaseLinkerJson>) falseFtpStatusOrdersJsons).build()), outputPath);
      } else {
        fileTranslation.executeTask(falseFtpStatusOrdersJsons, outputPath);
      }
      log.info(
          "Records with false ftp status found. Starting operation of sending pending files to FTP");
      ftpSend(outputPath.toString());
    } catch (Exception e) {
      log.error("Error occured during execution of ftp check scheduler {}", e.getMessage());
      throw new SchedulerOperationException("Error occured during execution of scheduler",
          e.getCause());
    } finally {
      if (outputPath != null && Files.exists(outputPath)) {
        FileHelper.clearAllDirectory(outputPath.toString());
      }
    }
    log.debug("Ftp  scheduler finished");
  }

  public void ftpSend(String localFolderPath)
      throws FtpServiceException, ServiceOperationException {
    ftpServicePromoOrders.uploadOrder(localFolderPath);
    List<String> orders = ftpServicePromoOrders.getUploadedOrderNumbersList().stream()
        .peek(s -> log.info(String.format("Order number %s sent to ftp", s)))
        .collect(Collectors.toList());
    if (databaseService.updateOrdersWhereFtpStatusIsFalseToTrueStatus(orders)) {
      log.debug("All orders have been updated to ftp flag true");
    } else {
      log.debug("No orders have been updated to ftp flag true");
    }
    log.debug("BASELINKER Scheduler finished all tasks");
  }

  @Override
  public void startup() {
    log.debug("Scheduler started");
  }
}
