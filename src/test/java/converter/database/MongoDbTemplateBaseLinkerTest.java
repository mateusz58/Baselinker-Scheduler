package converter.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.exceptions.DatabaseOperationException;
import converter.helper.DateTimeFunctions;
import converter.mapper.ObjectTranslator;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerNosql;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.NoSql.OrderNosql;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedDataSourceConfiguration.class)
class MongoDbTemplateBaseLinkerTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");
  private static List<OrderNosql> ordersBaseLinkerNoSqlListImportedSampleForTesting = new ArrayList<OrderNosql>();
  @Autowired
  MongoTemplate mongoTemplate;
  private MongoDbBaseLinkerOrders mongoDbBaseLinker;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeAll
  static void obtainTestObjectsToSaveToDatabase() throws IOException, ParseException {
    DataInputStream dataInputStream = new DataInputStream(
        new FileInputStream(FILES_BASELINKER_TESTING.BASE_LINKER_JSON_MANY_ORDERS));
    OrdersBaseLinkerJson ordersBaseLinkerJson = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        dataInputStream, OrdersBaseLinkerJson.class, FileFormatType.JSON);
    dataInputStream.close();
    ordersBaseLinkerNoSqlListImportedSampleForTesting = ObjectTranslator.translate(
        new BaseLinkerJsonToBaseLinkerNosql(), ordersBaseLinkerJson.getOrders());
  }

  @BeforeEach
  void setUp() throws DatabaseOperationException {
    this.mongoDbBaseLinker = new MongoDbBaseLinkerOrders(mongoTemplate);
    for (int i = 0; i < ordersBaseLinkerNoSqlListImportedSampleForTesting.size(); i++) {
      mongoTemplate.save(ordersBaseLinkerNoSqlListImportedSampleForTesting.get(i));
    }
  }

  @AfterEach
  void tearup() throws DatabaseOperationException {
    mongoTemplate.dropCollection(OrderNosql.class);
  }

  @Test
  void checkIfRecordsWereAddedToDatabase() {
    assertTrue(mongoTemplate.findAll(OrderNosql.class).size() > 0);
  }

  @Test
  void getAllFunctionShouldReturnAllObtainedOrders() throws DatabaseOperationException {
    assertEquals(ordersBaseLinkerNoSqlListImportedSampleForTesting, mongoDbBaseLinker.getAll());
  }

  @Test
  void addFunctionShouldAddNewOrderToDatabaseAndCheckIfOrderWasInserted()
      throws DatabaseOperationException {
    OrderNosql expected = ordersBaseLinkerNoSqlListImportedSampleForTesting.get(0).toBuilder()
        .mongoId(null).orderId("NEW_ORDER_ADDED").addressCity("NEW_CITY_ADDED")
        .addressCompany("NEW_CITY_ADDED").build();

    mongoDbBaseLinker.add(expected);
    Query query = new Query();
    query.limit(1);
    query.with(Sort.by(Sort.Direction.DESC, "date_added"));
    OrderNosql actual = mongoTemplate.findOne(query, OrderNosql.class);

    assertEquals(expected, actual);
  }

  @Test
  void updateAllWhereGivenKeyMatchesSelectedValueToNewValueFunctionShouldUpdateAllRecordsWhereFtpStatusIsFalseToTrue()
      throws DatabaseOperationException {
    List<OrderNosql> expected = new ArrayList<>();
    for (int i = 0; i < ordersBaseLinkerNoSqlListImportedSampleForTesting.size(); i++) {
      expected.add(
          ordersBaseLinkerNoSqlListImportedSampleForTesting.get(i).toBuilder().ftp(true).build());
    }
    mongoDbBaseLinker.updateWhereGivenKeyMatchesSelectedValueToNewValue("ftp", false, true);
    assertEquals(expected, mongoTemplate.findAll(OrderNosql.class));
  }

  @Test
  void findAllByKeyAndGivenValueShouldReturnAllRecordsWhereFtpStatusIsFalse()
      throws DatabaseOperationException {
    //given
    List<OrderNosql> expected = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      OrderNosql added = ordersBaseLinkerNoSqlListImportedSampleForTesting.get(i).toBuilder()
          .ftp(true).build();
      expected.add(added);
      mongoTemplate.save(added);
    }

    //actual
    List<OrderNosql> actual = mongoDbBaseLinker.findAllByKeyAndGivenValue("ftp", true);

    //then
    assertEquals(expected, actual);
  }

  @Test
  void addFunctionShouldUpdateOrderToDatabaseWhenOrderWithDuplicateOrderIdWasInserted()
      throws DatabaseOperationException {
    String duplicateOrderId = ordersBaseLinkerNoSqlListImportedSampleForTesting.get(0).getOrderId();
    OrderNosql expected = ordersBaseLinkerNoSqlListImportedSampleForTesting.get(0);
    expected.setOrderSource("changed");

    mongoDbBaseLinker.add(expected);
    OrderNosql actual = mongoTemplate.findOne(
        Query.query(Criteria.where("orderId").is(duplicateOrderId)), OrderNosql.class,
        "ordersBaselinker");

    assertEquals(expected, actual);
  }


  @Test
  void searchByIdFunctionShouldSearchOrderById() throws DatabaseOperationException {
    String expected = "BL72002699";

    OrderNosql actual = mongoDbBaseLinker.getById(expected);

    assertEquals(expected, actual.getOrderId());
  }

  @Test
  void getAllOrdersAfterDateShouldReturnAllOrdersGreaterThanGivenParametreGivenLocalDateTime()
      throws DatabaseOperationException {

    LocalDateTime given = LocalDateTime.of(2022, 12, 1, 0, 0, 0);
    List<OrderNosql> expected = ordersBaseLinkerNoSqlListImportedSampleForTesting.stream()
        .filter(i -> i.getOrderDateAdd().isAfter(given)).collect(Collectors.toList());

    List<OrderNosql> actual = mongoDbBaseLinker.getAllAfterDate(given);

    assertTrue(!actual.isEmpty());
    assertTrue(actual.stream().noneMatch(s -> s.getOrderDateAdd().isBefore(given)));
  }

  @Test
  void getAllOrdersAfterTimestampFunctionShouldReturnAllOrdersGreaterThanGivenParametres()
      throws DatabaseOperationException {
    Long givenTimestamp = 1670612701L; // Fri Dec 09 2022 19:05:01 GMT
    List<OrderNosql> expected = ordersBaseLinkerNoSqlListImportedSampleForTesting.stream()
        .filter(i -> i.getTimestampGmt() > givenTimestamp).collect(Collectors.toList());

    List<OrderNosql> actual = mongoDbBaseLinker.getAllOrdersAfterTimeStamp(givenTimestamp);

    assertEquals(expected, actual);
  }

  @Test
  void getLatestFunctionShouldReturnLastInsertedOrder() throws DatabaseOperationException {
    LocalDateTime currentTimestamp = LocalDateTime.now().withNano(0);
    Long timestamp = DateTimeFunctions.convertLocalDateTimeToUnixTimeStamp(currentTimestamp);
    OrderNosql expected = ordersBaseLinkerNoSqlListImportedSampleForTesting.get(0).toBuilder()
        .mongoId(null).orderId("CHANGED_ORDER_NUMBER").addressCity("CHANGED_CITY")
        .timestampGmt(timestamp).build();

    mongoTemplate.save(expected);
    OrderNosql actual = mongoDbBaseLinker.getLatest();

    assertEquals(expected, actual);
  }

  @Test
  void deleteAllFunctionTest() throws DatabaseOperationException {
    mongoTemplate.dropCollection(OrderNosql.class);
    List<OrderNosql> list = mongoTemplate.findAll(OrderNosql.class);
    assertEquals(list.isEmpty(), true);
  }
}
