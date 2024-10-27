package converter.scheduler.stocks;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.atLeastOnce;
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
    "baselinker.schedulers.stocksFullGetter.cron=0 0 * * * *",
    "baselinker.schedulers.stocksDiffGetter.cron=0 0 * * * *",
    "baselinker.schedulers.inventory.cron=*/30 * * * * *",
    "baselinker.schedulers.orders.enabled=false"
})
@ActiveProfiles("dev")
class BaselinkerInventoryLoadSchedulerTest {

  @SpyBean
  BaselinkerStocksScheduler baselinkerStocksScheduler;

  @Test
  public void loadInventoryShouldBeCalledAtleastOneTimeWithoutErrors()
      throws ServiceOperationException {
    try {
      baselinkerStocksScheduler.inventoryLoader();
    } catch (Exception e) {
      fail("Unexpected exception thrown during inventory loading: " + e.getMessage());
    }

    // verify that the scheduler was used at least once
    verify(baselinkerStocksScheduler, atLeastOnce()).inventoryLoader();
    ;
  }
}
