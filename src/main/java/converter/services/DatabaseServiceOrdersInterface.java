package converter.services;

import converter.exceptions.DatabaseOperationException;
import converter.exceptions.ServiceOperationException;
import java.text.ParseException;
import java.util.List;

public interface DatabaseServiceOrdersInterface<Y> extends DatabaseServiceInterface<Y> {

  Y getLatestOrder() throws ServiceOperationException;

  List<Y> findAllOrdersWhereFtpStatusIsFalse()
      throws ServiceOperationException, DatabaseOperationException, ParseException;

  boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus() throws ServiceOperationException;

  boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus(String orderId)
      throws ServiceOperationException;

  boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus(List<String> order)
      throws ServiceOperationException;
}
