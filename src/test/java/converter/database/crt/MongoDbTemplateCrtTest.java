package converter.database.crt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.CRT.FILES_TESTING_CRT;
import converter.configuration.FileFormatType;
import converter.database.MongoDbCrt;
import converter.exceptions.DatabaseOperationException;
import converter.helper.FileHelper;
import converter.mapper.ObjectTranslator;
import converter.mapper.crt.CrtJsonToNoSql;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.Item;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.CrtModel.JSON.StateOrderEnum;
import converter.model.CrtModel.NoSql.EventCrtNoSql;
import converter.model.CrtModel.NoSql.OrderCrtNoSql;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataMongoTest
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
class MongoDbTemplateCrtTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");
  private static List<OrderCrtNoSql> ordersNoSqlList = new ArrayList<OrderCrtNoSql>();

  private static List<OrderCrtNoSql> ordersMappedFromLoadedEventToUpdateSingleOrder = new ArrayList<OrderCrtNoSql>();
  private static List<EventCrtJson> loadedEventsCrtJson = new ArrayList<EventCrtJson>();
  private static OrderCrtNoSql orderCrtNoSql;
  @Autowired
  MongoTemplate mongoTemplate;

  private MongoDbCrt mongoDbCrt;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeAll
  static void obtainTestObjectsToSaveToDatabase() throws IOException, ParseException {
    OrderCrtJson orderCrtJson = (OrderCrtJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_TESTING_CRT.CRT_DATABABASE_ORDER)),
        OrderCrtJson.class, FileFormatType.JSON);
    orderCrtNoSql = ObjectTranslator.translate(new CrtJsonToNoSql(), orderCrtJson);
    ordersNoSqlList.add(orderCrtNoSql);
  }

  private OrderCrtJson mapEventToOrder(EventCrtJson event) {
    return OrderCrtJson.builder()
        .orderNumber(event.getOrderNumber())
        .state(event.getState())
        .orderId(event.getOrderId())
        .events(List.of(event))
        .items(event.getItems())
        .build();
  }

  private void loadEvents() throws IOException, ParseException {
    loadedEventsCrtJson.clear();
    List<String> eventFiles = FileHelper.listAllFilePathsFromSelectedDirectory(
        FILES_TESTING_CRT.CRT_EVENTS_directory);
    List<OrderCrtJson> ordersTranslatedFromEvents = new LinkedList<>();
    for (int i = 0; i < eventFiles.size(); i++) {
      EventCrtJson eventCrtJson = (EventCrtJson) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(eventFiles.get(i))), EventCrtJson.class,
          FileFormatType.JSON);
      loadedEventsCrtJson.add(eventCrtJson);
      ordersTranslatedFromEvents.add((mapEventToOrder(eventCrtJson)));
    }
    ordersMappedFromLoadedEventToUpdateSingleOrder = ObjectTranslator.translate(
        new CrtJsonToNoSql(), ordersTranslatedFromEvents);
  }

  @BeforeEach
  void setUp() throws IOException, ParseException {
    loadEvents();
    this.mongoDbCrt = new MongoDbCrt(mongoTemplate);
    for (OrderCrtNoSql orderCrtNoSql : ordersNoSqlList) {
      mongoTemplate.save(orderCrtNoSql);
    }
  }

  @AfterEach
  void tearup() throws DatabaseOperationException {
    mongoTemplate.dropCollection(OrderCrtNoSql.class);
  }

  @Test
  void checkIfRecordsWereAddedToDatabase() {
    assertTrue(mongoTemplate.findAll(OrderCrtNoSql.class).size() > 0);
  }

  @Test
  void getAllFunctionShouldReturnAllObtainedOrders() throws DatabaseOperationException {
    List<OrderCrtNoSql> actual = mongoDbCrt.getAll();
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(ordersNoSqlList.get(i), actual.get(i));
    }
  }

  @Test
  void addFunctionShouldAddNewOrderToDatabaseAndReturnLastInsertedOrder()
      throws DatabaseOperationException {
    OrderCrtNoSql expected = ordersNoSqlList.get(0).toBuilder().mongoId(null).orderId("order-3")
        .orderNumber("order_updated").build();

    mongoDbCrt.add(expected);
    Query query = new Query();
    query.limit(1);
    query.with(Sort.by(Sort.Direction.DESC, "dateAdded"));
    OrderCrtNoSql actual = mongoTemplate.findOne(query, OrderCrtNoSql.class);

    assertTrue(actual.equals(expected));
  }

  @Test
  void addFunctionShouldUpdateOrderToDatabaseWhenOrderWithDuplicateOrderIdWasInserted()
      throws DatabaseOperationException {
    OrderCrtNoSql expected = ordersNoSqlList.get(0).toBuilder().state(StateOrderEnum.returned)
        .build();

    mongoDbCrt.add(expected);
    OrderCrtNoSql actual = mongoTemplate.findOne(
        Query.query(Criteria.where("orderNumber").is(expected.getOrderNumber())),
        OrderCrtNoSql.class, "ordersCrt");

    assertEquals(expected, actual);
  }

  @Test
  void searchByIdFunctionShouldSearchOrderById() throws DatabaseOperationException {
    String expected = ordersNoSqlList.get(0).getOrderId();

    OrderCrtNoSql actual = mongoDbCrt.getById(expected);

    assertTrue(expected.equals(actual.getOrderId()));
  }

  @Test
  void checkIfAllCanBeDeleted() throws DatabaseOperationException {
    mongoTemplate.dropCollection(OrderCrtNoSql.class);
    List<OrderCrtNoSql> list = mongoTemplate.findAll(OrderCrtNoSql.class);
    assertEquals(list.isEmpty(), true);
  }

  @Test
  void changeOrderByUpcomingEventShouldUpdateOrderByUpcomingEvent()
      throws DatabaseOperationException {
    //given
    OrderCrtNoSql orderBeforeUpdate = ordersNoSqlList.get(0);
    OrderCrtNoSql newEventOrderGivenToAdd = ordersMappedFromLoadedEventToUpdateSingleOrder.stream()
        .filter(s -> s.getState() == StateOrderEnum.cancelled).findFirst().get();

    List<EventCrtNoSql> updatedEventExpectedlist = new LinkedList<>();
    updatedEventExpectedlist.addAll(orderCrtNoSql.getEventCrtNoSqlList());
    updatedEventExpectedlist.add(newEventOrderGivenToAdd.getEventCrtNoSqlList().get(0));

    List<Item> updatedItemsExpectedList = new LinkedList<>();
    updatedItemsExpectedList.addAll(orderCrtNoSql.getItems());
    updatedItemsExpectedList.addAll(newEventOrderGivenToAdd.getItems());
    OrderCrtNoSql expected = ordersNoSqlList.get(0).toBuilder()
        .state(newEventOrderGivenToAdd.getState()).eventCrtNoSqlList(updatedEventExpectedlist)
        .items(newEventOrderGivenToAdd.getItems()).build();

    //actual
    OrderCrtNoSql actual = mongoDbCrt.add(newEventOrderGivenToAdd);

    //then
    assertEquals(expected, actual);
  }
}
