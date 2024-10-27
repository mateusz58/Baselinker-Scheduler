package converter.scheduler.stocks;


import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import converter.Application;
import converter.exceptions.ServiceOperationException;
import converter.schedulers.baselinker.BaselinkerStocksScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// cron job every second

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "baselinker.schedulers.stocksFullGetter.cron=*/50 * * * * *",
    "baselinker.schedulers.stocksDiffGetter.cron=0 0 * * * *",
    "baselinker.schedulers.inventory.cron=0 0 * * * *",
    "baselinker.schedulers.orders.enabled=false"
})
@ActiveProfiles("dev")
class BaselinkerStocksFullSchedulerTest {

  @SpyBean
  BaselinkerStocksScheduler baselinkerStocksScheduler;

  @Test
  public void stockCompleteShouldBeCalledOneTimeWithoutErrors() throws ServiceOperationException {
    await()
        .untilAsserted(() -> verify(baselinkerStocksScheduler, times(1)).stocksCompleteLoader());
  }
}
