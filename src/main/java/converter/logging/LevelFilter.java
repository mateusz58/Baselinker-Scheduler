package converter.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.ArrayList;
import java.util.List;

public class LevelFilter extends AbstractMatcherFilter<ILoggingEvent> {

  List<Level> levels = new ArrayList<Level>();

  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    if (levels.contains(event.getLevel())) {
      return onMatch;
    } else {
      return onMismatch;
    }
  }

  public void setLevel(Level level) {
    levels.add(level);
  }
}