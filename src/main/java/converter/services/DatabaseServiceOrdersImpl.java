package converter.services;

import converter.database.DatabaseOrders;
import converter.exceptions.DatabaseOperationException;
import converter.exceptions.ServiceOperationException;
import converter.mapper.Mapper;
import converter.mapper.ObjectTranslator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseServiceOrdersImpl<X, Y> implements DatabaseServiceOrdersInterface<Y> {

  protected Logger log = LoggerFactory.getLogger(getClass());
  DatabaseOrders<X> database;
  Mapper<X, Y> mapper;
  Mapper<Y, X> mapperReversed;

  public DatabaseServiceOrdersImpl(DatabaseOrders<X> database, Mapper<X, Y> mapper,
      Mapper<Y, X> mapperReversed) {
    this.database = database;
    this.mapperReversed = mapperReversed;
    this.mapper = mapper;
  }

  public Y add(Y object)
      throws ParseException, DatabaseOperationException, ServiceOperationException {
    if (object == null) {
      log.error("Object is null");
      throw new IllegalArgumentException("Object is null");
    }
    Y objectToReturn = null;
    try {
      X objectMappedToDatabaseEntity = Optional.ofNullable(
          database.add(ObjectTranslator.translate(mapperReversed, object))).orElseThrow(
          () -> new ServiceOperationException("Error while adding object to database"));
      objectToReturn = ObjectTranslator.translate(mapper, objectMappedToDatabaseEntity);
    } catch (DatabaseOperationException | ParseException e) {
      throw new ServiceOperationException("Error while adding object to database", e);
    }
    return objectToReturn;
  }

  public List<Y> getAll() throws ServiceOperationException {
    List<Y> result = null;
    try {
      List<X> temp = Optional.ofNullable(database.getAll())
          .orElseThrow(() -> new ServiceOperationException("No orders found in database"));
      result = ObjectTranslator.translate(mapper, temp);
    } catch (DatabaseOperationException | ParseException e) {
      throw new ServiceOperationException("Error occurred while getting all objects from database",
          e);
    }
    return result;
  }

  public Y getLatestOrder() throws ServiceOperationException {
    Y objectToReturn = null;
    try {
      X object = database.getLatest();
      if (Optional.ofNullable(object).isPresent()) {
        objectToReturn = ObjectTranslator.translate(mapper, database.getLatest());
      }
    } catch (ParseException | DatabaseOperationException e) {
      throw new ServiceOperationException(
          "Error occurred while looking for orders with false ftp status", e);
    }
    return objectToReturn;
  }

  public List<Y> findAllOrdersWhereFtpStatusIsFalse()
      throws ServiceOperationException, DatabaseOperationException, ParseException {
    List<Y> objectToReturn = new ArrayList<>();
    objectToReturn = ObjectTranslator.translate(mapper,
        Optional.ofNullable(database.findAllByKeyAndGivenValue("ftp", false)).orElseThrow(
            () -> new ServiceOperationException("No orders in database with false ftp status")));
    ;
    return objectToReturn;
  }

  public boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus() throws ServiceOperationException {
    try {
      return database.updateWhereGivenKeyMatchesSelectedValueToNewValue("ftp", false, true);
    } catch (DatabaseOperationException e) {
      throw new ServiceOperationException(
          "Error occurred while updating all orders with false with ftp status to true status", e);
    }
  }

  public boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus(String orderId)
      throws ServiceOperationException {
    if (orderId == null) {
      log.error("Order id is null");
      throw new IllegalArgumentException("Order id is null");
    }
    try {
      return database.updateWhereGivenKeyMatchesSelectedValueToNewValue(orderId, "ftp", false,
          true);
    } catch (DatabaseOperationException e) {
      throw new ServiceOperationException(String.format(
          "Error occurred while updating order with id %s with flase with ftp status to true status",
          orderId), e);
    }
  }

  public boolean updateOrdersWhereFtpStatusIsFalseToTrueStatus(List<String> orderIds)
      throws ServiceOperationException {
    if (orderIds == null) {
      log.error("Order ids are null");
      throw new IllegalArgumentException("Order ids are null");
    }
    try {
      return database.updateWhereGivenKeyMatchesSelectedValueToNewValue(orderIds, "ftp", false,
          true);
    } catch (DatabaseOperationException e) {
      throw new ServiceOperationException(
          "Error occurred while updating orders with with false ftp status to true status", e);
    }
  }
}
