package converter.scheduler.stocks;


import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import converter.Application;
import converter.exceptions.ServiceOperationException;
import converter.schedulers.baselinker.BaselinkerStocksScheduler;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// cron job every second

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "baselinker.schedulers.stocksFullGetter.cron=0 0 * * * *",
    "baselinker.schedulers.stocksDiffGetter.cron=*/30 * * * * *",
    "baselinker.schedulers.inventory.cron=0 0 * * * *",
    "baselinker.schedulers.orders.enabled=false"
})
@ActiveProfiles("dev")
class BaselinkerStocksDiffSchedulerTest {

  @SpyBean
  BaselinkerStocksScheduler baselinkerStocksScheduler;

  @Test
  public void stockDiffShouldBeCalledTwoTimesWithoutErrors() throws ServiceOperationException {
    await()
        .atMost(Duration.ofSeconds(120))
        .untilAsserted(() -> verify(baselinkerStocksScheduler, atLeast(2)).stocksDiffLoader());
  }
}