package converter.database;

import converter.exceptions.DatabaseOperationException;
import java.util.List;

public interface Database<X> {

  X getById(String id) throws DatabaseOperationException;

  List<X> getAll() throws DatabaseOperationException;

  X add(X object) throws DatabaseOperationException;

  List<X> findAllByKeyAndGivenValue(String key, Object value) throws DatabaseOperationException;
}
