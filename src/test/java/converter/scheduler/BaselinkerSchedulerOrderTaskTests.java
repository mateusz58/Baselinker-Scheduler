package converter.scheduler;


import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import converter.configuration.Schedulers.SchedulersConfiguration;
import converter.schedulers.baselinker.BaselinkerOrdersScheduler;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// cron job every second
@SpringJUnitConfig(SchedulersConfiguration.class)
@SpringBootTest(properties = {
    "${base-linker-scheduler}=*/20 * * * * *"
})
class BaselinkerSchedulerOrderTaskTests {

  @SpyBean
  BaselinkerOrdersScheduler scheduledTasks;

  @Test
  public void whenWaitFiveSecond_thenScheduledIsCalledAtLeastOneTime() {
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(() -> verify(scheduledTasks, atLeast(1)).loadOrders());
  }
}