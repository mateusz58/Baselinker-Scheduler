package converter.configuration.Schedulers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@ComponentScan
@ConditionalOnProperty(name = "schedulers.enabled", matchIfMissing = true)
public class SchedulersConfiguration implements SchedulingConfigurer {

  @Value("${scheduler.thread.pool.size}")
  private int SCHEDULER_THREAD_POOL_SIZE;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    threadPoolTaskScheduler.setPoolSize(SCHEDULER_THREAD_POOL_SIZE);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    threadPoolTaskScheduler.initialize();

    taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
  }
}
