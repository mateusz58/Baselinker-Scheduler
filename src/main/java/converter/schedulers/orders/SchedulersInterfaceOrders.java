package converter.schedulers.orders;

import converter.exceptions.FtpServiceException;
import converter.exceptions.SchedulerOperationException;
import converter.exceptions.ServiceOperationException;
import converter.schedulers.SchedulerInterface;
import java.io.IOException;

public interface SchedulersInterfaceOrders extends SchedulerInterface {

  void ftpCheck() throws SchedulerOperationException, IOException;

  void ftpSend(String localFolderPath)
      throws SchedulerOperationException, IOException, FtpServiceException, ServiceOperationException;
}
