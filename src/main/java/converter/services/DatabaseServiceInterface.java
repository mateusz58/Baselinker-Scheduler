package converter.services;

import converter.exceptions.DatabaseOperationException;
import converter.exceptions.ServiceOperationException;
import java.text.ParseException;
import java.util.List;

public interface DatabaseServiceInterface<Y> {

  Y add(Y add) throws ParseException, DatabaseOperationException, ServiceOperationException;

  List<Y> getAll() throws ServiceOperationException;
}
