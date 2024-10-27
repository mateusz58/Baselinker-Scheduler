package converter.schedulers;

import converter.exceptions.FtpServiceException;
import converter.exceptions.SchedulerOperationException;
import converter.exceptions.ServiceOperationException;
import java.io.IOException;
import java.text.ParseException;

public interface SchedulerInterface {

  void startup()
      throws SchedulerOperationException, IOException, ServiceOperationException, ParseException, FtpServiceException;
}
