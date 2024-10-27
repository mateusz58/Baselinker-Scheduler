package converter.schedulers.CRT;

import converter.database.MongoDbCrt;
import converter.exceptions.SchedulerOperationException;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.schedulers.orders.SchedulerOrdersImpl;
import converter.schedulers.orders.SchedulersInterfaceOrders;
import converter.services.DatabaseServiceOrdersInterface;
import converter.services.FileTranslation;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.IOException;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "crt.schedulers.enabled", havingValue = "true", matchIfMissing = true)
public class CrtOrdersScheduler {

  private static final Logger log = LoggerFactory.getLogger(CrtOrdersScheduler.class);

  FileTranslation fileTranslationCrt;
  FtpServicePromoOrders ftpServicePromoOrders;
  DatabaseServiceOrdersInterface<OrderCrtJson> databaseService;
  SchedulersInterfaceOrders schedulersInterfaceOrders;
  MongoDbCrt mongoDbCrt;

  public CrtOrdersScheduler(@Qualifier("CRT-FILETRANSLATION") FileTranslation fileTranslationCrt,
      FtpServicePromoOrders ftpServicePromoOrders, MongoDbCrt mongoDbCrt,
      @Qualifier("CRT-DATABASE-SERVICE") DatabaseServiceOrdersInterface databaseService) {
    this.fileTranslationCrt = fileTranslationCrt;
    this.ftpServicePromoOrders = ftpServicePromoOrders;
    this.mongoDbCrt = mongoDbCrt;
    this.databaseService = databaseService;
    schedulersInterfaceOrders = new SchedulerOrdersImpl(databaseService, fileTranslationCrt,
        ftpServicePromoOrders);
  }

  @Scheduled(cron = "${CRT.ftpCheckScheduler}", zone = "Europe/Warsaw")
  @SchedulerLock(
      name = "ftpCheckTaskCRT",
      lockAtLeastFor = "${CRT.scheduler.lockMinimalTime}",
      lockAtMostFor = "${CRT.scheduler.lockMaxTime}"
  )
  public void ftpCheck() {
    try {
      schedulersInterfaceOrders.ftpCheck();
    } catch (SchedulerOperationException | IOException e) {
      log.error("Error in ftpCheckTaskCRT: " + e.getMessage());
    }
  }
}
