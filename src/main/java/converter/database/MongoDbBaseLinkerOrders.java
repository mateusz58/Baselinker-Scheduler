package converter.database;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.result.UpdateResult;
import converter.exceptions.DatabaseOperationException;
import converter.model.baseLinkerModel.NoSql.OrderNosql;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "baselinker.database.enabled", havingValue = "true", matchIfMissing = true)
public class MongoDbBaseLinkerOrders implements DatabaseOrders<OrderNosql> {

  private static final Logger log = (Logger) LoggerFactory.getLogger(MongoDbBaseLinkerOrders.class);

  private MongoTemplate mongoTemplate;

  @Value("${spring.data.mongodb.uri}")
  private String uri;

  public MongoDbBaseLinkerOrders(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public OrderNosql getById(String parameter) throws DatabaseOperationException {
    if (parameter == null) {
      throw new IllegalArgumentException("Parameter cannot be null.");
    }
    OrderNosql order;
    try {
      order = mongoTemplate.findOne(Query.query(Criteria.where("order_id").is(parameter)),
          OrderNosql.class, "ordersBaselinker");
    } catch (Exception e) {
      String message = "An error occurred during getting last order.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
    return order;
  }

  @Override
  public OrderNosql getLatest() throws DatabaseOperationException {
    OrderNosql lastOrder;
    try {
      Query query = new Query();
      query.limit(1);
      query.with(Sort.by(Direction.DESC, "timestamp_GMT"));
      lastOrder = mongoTemplate.findOne(query, OrderNosql.class);
    } catch (Exception e) {
      String message = "An error occurred during getting last order.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
    return lastOrder;
  }

  @Override
  public List<OrderNosql> findAllByKeyAndGivenValue(String key, Object value)
      throws DatabaseOperationException {
    List<OrderNosql> orderNosqlList = new ArrayList<>();
    try {
      orderNosqlList = mongoTemplate.find(Query.query(Criteria.where(key).is(value)),
          OrderNosql.class);
    } catch (Exception e) {
      log.error("An error occured: {}", e);
      throw new DatabaseOperationException(String.format("An error occured: %s", e.getMessage()));
    }
    return orderNosqlList;
  }


  @Override
  public boolean updateWhereGivenKeyMatchesSelectedValueToNewValue(String fieldName,
      Object oldValue, Object newValue) throws DatabaseOperationException {
    Query query = new Query().addCriteria(new Criteria(fieldName).is(oldValue));
    Update update = new Update().set(fieldName, newValue);
    UpdateResult updateResult;
    try {
      updateResult = mongoTemplate.updateMulti(query, update, OrderNosql.class);
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
      updateResult = mongoTemplate.updateMulti(query, update, OrderNosql.class);
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
      updateResult = mongoTemplate.updateMulti(query, update, OrderNosql.class);
    } catch (Exception e) {
      log.error("Error occured: {}", e.getMessage());
      throw new DatabaseOperationException("Exception occured during updating database records", e);
    }
    return updateResult.wasAcknowledged();
  }

  @Override
  public List<OrderNosql> getAllAfterDate(LocalDateTime parameter)
      throws DatabaseOperationException {
    if (parameter == null) {
      log.info("Attempt to get order when data parameter is null");
      throw new IllegalArgumentException("Date parameter cannot be null");
    }
    try {
      return mongoTemplate.find(Query.query(Criteria.where("order_date_add").gt(parameter)),
          OrderNosql.class);
    } catch (Exception e) {
      String message = "An error occurred during getting orders filtered by issued date.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }

  public List<OrderNosql> getAllOrdersAfterTimeStamp(Long parameter)
      throws DatabaseOperationException {
    if (parameter == null) {
      log.info("Attempt to get order when data parameter is null");
      throw new IllegalArgumentException("Date parameter cannot be null");
    }
    try {
      return mongoTemplate.find(Query.query(Criteria.where("timestampGmt").gt(parameter)),
          OrderNosql.class);
    } catch (Exception e) {
      String message = "An error occurred during getting orders filtered by issued date.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }

  @Override
  public List<OrderNosql> getAll() throws DatabaseOperationException {
    try {
      return mongoTemplate.findAll(OrderNosql.class, "ordersBaselinker");
    } catch (Exception e) {
      String message = "An error occurred during getting all orders.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }

  private OrderNosql update(OrderNosql orderBaseLinkerNoSql, String mongoId) {
    OrderNosql updated = OrderNosql.builder()
        .mongoId(mongoId)
        .orderId(orderBaseLinkerNoSql.getOrderId())
        .orderDateAdd(orderBaseLinkerNoSql.getOrderDateAdd())
        .orderSource(orderBaseLinkerNoSql.getOrderSource())
        .orderAmountBrutto(orderBaseLinkerNoSql.getOrderAmountBrutto())
        .address(orderBaseLinkerNoSql.getAddress())
        .addressCompany(orderBaseLinkerNoSql.getAddressCompany())
        .addressCountryCode(orderBaseLinkerNoSql.getAddressCountryCode())
        .addressName(orderBaseLinkerNoSql.getAddressName())
        .addressCity(orderBaseLinkerNoSql.getAddressCity())
        .address(orderBaseLinkerNoSql.getAddress())
        .addressPostal(orderBaseLinkerNoSql.getAddressPostal())
        .clientAddress(orderBaseLinkerNoSql.getClientAddress())
        .clientCompany(orderBaseLinkerNoSql.getClientCompany())
        .clientCity(orderBaseLinkerNoSql.getClientCity())
        .clientPostal(orderBaseLinkerNoSql.getClientPostal())
        .clientLogin(orderBaseLinkerNoSql.getClientLogin())
        .clientName(orderBaseLinkerNoSql.getClientName())
        .clientNip(orderBaseLinkerNoSql.getClientNip())
        .deliveryType(orderBaseLinkerNoSql.getDeliveryType())
        .deliveryPrice(orderBaseLinkerNoSql.getDeliveryPrice())
        .paymentPaid(orderBaseLinkerNoSql.getPaymentPaid())
        .paymentStatus(orderBaseLinkerNoSql.getPaymentStatus())
        .timestampGmt(orderBaseLinkerNoSql.getTimestampGmt())
        .itemNoSqlList(orderBaseLinkerNoSql.getItemNoSqlList())
        .orderPage(orderBaseLinkerNoSql.getOrderPage())
        .build();
    return mongoTemplate.save(updated);
  }

  @Override
  public OrderNosql add(OrderNosql object) throws DatabaseOperationException {
    if (object == null) {
      log.error("Attempt to save null Order");
      throw new IllegalArgumentException("Order cannot be null.");
    }
    try {
      Optional<OrderNosql> optional = Optional.ofNullable(
          mongoTemplate.findOne(Query.query(Criteria.where("order_id").is(object.getOrderId())),
              OrderNosql.class, "ordersBaselinker"));
      if (optional.isEmpty()) {
        log.info("Adding new order to database with number: {}", object.getOrderId());
        return mongoTemplate.save(object);
      } else {
        log.info("Updating order in database with number: {}", object.getOrderId());
        return update(object, optional.get().getMongoId());
      }
    } catch (Exception e) {
      String message = "An error occurred during saving order.";
      log.error(message, e);
      throw new DatabaseOperationException(message, e);
    }
  }
}
