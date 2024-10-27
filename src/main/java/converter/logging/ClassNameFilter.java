package converter.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.LinkedList;
import java.util.List;

public class ClassNameFilter extends AbstractMatcherFilter<ILoggingEvent> {

  List<String> loggerNames = new LinkedList<>();

  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    if (loggerNames.contains(event.getLoggerName())) {
      return onMatch;
    } else {
      return onMismatch;
    }
  }
  public void setClassName(String className) {
    loggerNames.add(className);
  }
}
