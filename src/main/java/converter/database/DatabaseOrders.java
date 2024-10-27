package converter.database;

import converter.exceptions.DatabaseOperationException;
import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseOrders<X> extends Database<X> {

  List<X> getAllAfterDate(LocalDateTime parameter) throws DatabaseOperationException;

  X getLatest() throws DatabaseOperationException;

  boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(String field, Object oldValue,
      Object newValue) throws DatabaseOperationException;

  boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(Object object, String field,
      Object oldValue, Object newValue) throws DatabaseOperationException;

  boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(List<?> objects, String field,
      Object oldValue, Object newValue) throws DatabaseOperationException;
}
