package converter.database;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.result.UpdateResult;
import converter.exceptions.DatabaseOperationException;
import converter.model.CrtModel.JSON.Item;
import converter.model.CrtModel.JSON.StateOrderEnum;
import converter.model.CrtModel.NoSql.EventCrtNoSql;
import converter.model.CrtModel.NoSql.OrderCrtNoSql;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "CRT.database.enabled", havingValue = "true", matchIfMissing = true)
public class MongoDbCrt implements DatabaseOrders<OrderCrtNoSql> {

  private static final Logger log = (Logger) LoggerFactory.getLogger(MongoDbCrt.class);

  private MongoTemplate mongoTemplate;

  public MongoDbCrt(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public List<OrderCrtNoSql> getAllAfterDate(LocalDateTime parameter)
      throws DatabaseOperationException {
    if (parameter == null) {
      log.error("Attempt to get order when data parameter is null");
      throw new IllegalArgumentException("Date parameter cannot be null");
    }
    try {
      return mongoTemplate.find(Query.query(Criteria.where("order_date_add").gt(parameter)),
          OrderCrtNoSql.class);
    } catch (Exception e) {
      String message = "An error occurred during getting orders filtered by issued date.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }

  @Override
  public OrderCrtNoSql getById(String parameter) throws DatabaseOperationException {
    if (parameter == null) {
      throw new IllegalArgumentException("Parameter cannot be null.");
    }
    OrderCrtNoSql order;
    try {
      order = mongoTemplate.findOne(Query.query(Criteria.where("order_number").is(parameter)),
          OrderCrtNoSql.class, "ordersCrt");
    } catch (Exception e) {
      String message = "An error occurred during getting order.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
    return order;
  }

  @Override
  public OrderCrtNoSql getLatest() throws DatabaseOperationException {
    OrderCrtNoSql lastOrder;
    try {
      Query query = new Query();
      query.limit(1);
      query.with(Sort.by(Sort.Direction.DESC, "date_added"));
      lastOrder = mongoTemplate.findOne(query, OrderCrtNoSql.class);
    } catch (Exception e) {
      String message = "An error occurred during getting last order.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
    return lastOrder;
  }

  @Override
  public List<OrderCrtNoSql> getAll() throws DatabaseOperationException {
    try {
      return mongoTemplate.findAll(OrderCrtNoSql.class, "ordersCrt");
    } catch (Exception e) {
      String message = "An error occurred during getting all orders.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }

  private List<Item> removeItemsIfOnTheListOfCancelled(List<Item> itemsReadFromDatabase,
      List<Item> itemsToRemove) {
    int j = 0;
    for (int i = 0; i < itemsToRemove.size(); i++) {
      int z = i;
      itemsReadFromDatabase.removeIf(s -> s.getItemId().equals(itemsToRemove.get(z).getItemId()));
    }
    return itemsReadFromDatabase;
  }

  private List<EventCrtNoSql> updateListEvent(List<EventCrtNoSql> listToBeUpdated,
      List<EventCrtNoSql> listToAddToListForUpdate) {
    listToBeUpdated.addAll(listToAddToListForUpdate);
    return listToBeUpdated.stream().distinct()
        .sorted(Comparator.comparing(EventCrtNoSql::getTimestamp).reversed())
        .collect(Collectors.toList());
  }

  @Override
  public boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(String fieldName,
      Object oldValue, Object newValue) throws DatabaseOperationException {
    Query query = new Query().addCriteria(new Criteria(fieldName).is(oldValue));
    Update update = new Update().set(fieldName, newValue);
    UpdateResult updateResult;
    try {
      updateResult = mongoTemplate.updateMulti(query, update, OrderCrtNoSql.class);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new DatabaseOperationException("Exception occured during updating database records", e);
    }
    return updateResult.wasAcknowledged();
  }

  @Override
  public boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(Object recordId,
      String fieldName, Object oldValue, Object newValue) throws DatabaseOperationException {
    Query query = new Query().addCriteria(new Criteria(fieldName).is(oldValue))
        .addCriteria(new Criteria("order_id").is(recordId));
    Update update = new Update().set(fieldName, newValue);
    UpdateResult updateResult;
    try {
      updateResult = mongoTemplate.updateMulti(query, update, OrderCrtNoSql.class);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new DatabaseOperationException("Exception occured during updating database records", e);
    }
    return updateResult.wasAcknowledged();
  }

  @Override
  public boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(List<?> recordsIds,
      String fieldName, Object oldValue, Object newValue) throws DatabaseOperationException {
    Query query = new Query().addCriteria(
        Criteria.where("order_id").in(recordsIds).and(fieldName).is(oldValue));
    Update update = new Update().set(fieldName, newValue);
    UpdateResult updateResult;
    try {
      updateResult = mongoTemplate.updateMulti(query, update, OrderCrtNoSql.class);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new DatabaseOperationException("Exception occured during updating database records", e);
    }
    return updateResult.wasAcknowledged();
  }


  private OrderCrtNoSql update(OrderCrtNoSql recordFromDatabase,
      OrderCrtNoSql newRecordWithNewData) {
    List<Item> updatedItemsList = new LinkedList<>();
    List<EventCrtNoSql> updatedEventList = updateListEvent(
        recordFromDatabase.getEventCrtNoSqlList(), newRecordWithNewData.getEventCrtNoSqlList());

    if (newRecordWithNewData.getState().equals(StateOrderEnum.cancelled)) {
      updatedItemsList = removeItemsIfOnTheListOfCancelled(recordFromDatabase.getItems(),
          newRecordWithNewData.getItems());
    }
    if (updatedItemsList.isEmpty()) {
      updatedItemsList = newRecordWithNewData.getItems();
    }
    OrderCrtNoSql updated = recordFromDatabase.toBuilder()
        .state(newRecordWithNewData.getState())
        .orderId(recordFromDatabase.getOrderId())
        .orderNumber(recordFromDatabase.getOrderNumber())
        .eventCrtNoSqlList(updatedEventList)
        .items(updatedItemsList)
        .build();
    return mongoTemplate.save(updated);
  }

  @Override
  public List<OrderCrtNoSql> findAllByKeyAndGivenValue(String key, Object value)
      throws DatabaseOperationException {
    if (key == null ^ value == null) {
      throw new IllegalArgumentException("Object argument is null");
    }
    List<OrderCrtNoSql> orderNosqlList = new ArrayList<>();
    try {
      orderNosqlList = mongoTemplate.find(Query.query(Criteria.where(key).is(value)),
          OrderCrtNoSql.class);
    } catch (Exception e) {
      log.error("An error occured: {}", e);
      throw new DatabaseOperationException(String.format("An error occured: %s", e.getMessage())
      );
    }
    return orderNosqlList;
  }

  @Override
  public OrderCrtNoSql add(OrderCrtNoSql objectToAdd) throws DatabaseOperationException {
    if (objectToAdd == null) {
      log.error("Attempt to save null Order");
      throw new IllegalArgumentException("Order cannot be null.");
    }
    try {
      Optional<OrderCrtNoSql> readRecordFromDatabase = Optional.ofNullable(mongoTemplate.findOne(
          Query.query(Criteria.where("order_number").is(objectToAdd.getOrderNumber())),
          OrderCrtNoSql.class, "ordersCrt"));
      if (readRecordFromDatabase.isEmpty()) {
        return mongoTemplate.save(objectToAdd);
      } else {
        return update(readRecordFromDatabase.get(), objectToAdd);
      }
    } catch (Exception e) {
      String message = "An error occurred during saving order.";
      log.error(message, e.getMessage());
      throw new DatabaseOperationException(message, e);
    }
  }
}
