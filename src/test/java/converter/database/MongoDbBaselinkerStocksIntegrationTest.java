package converter.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.exceptions.DatabaseOperationException;
import converter.model.stocks.ProductNoSql;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
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
public class MongoDbBaselinkerStocksIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

  @Autowired
  private MongoTemplate mongoTemplate;
  private MongoDbBaselinkerStocks mongoDbBaselinkerStocks;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeEach
  void setUpClass() {
    this.mongoDbBaselinkerStocks = new MongoDbBaselinkerStocks(mongoTemplate);
  }

  @AfterEach
  public void tearDownClass() {
    mongoDBContainer.stop();
  }

  @Test
  public void testLoadInventoryShouldAddAllProductToDatabaseWhenAllEansAreDifferent()
      throws DatabaseOperationException {
    // create some test data
    String inventoryId = "testInventory";
    List<ProductNoSql> expected = Arrays.asList(
        ProductNoSql.builder().id(123L).ean("1234567890123").sku("sku1").name("Product1").stock(10)
            .inventoryId("testInventory").price("45.0").build(),
        ProductNoSql.builder().id(123456L).ean("2345678901234").sku("sku2").name("Product2")
            .stock(20).inventoryId("testInventory").price("57.0").build()
    );
    // call the method under test
    mongoDbBaselinkerStocks.loadInventory(inventoryId, expected);
    // verify that the inventory was saved to MongoDB
    List<ProductNoSql> actual = mongoTemplate.findAll(ProductNoSql.class);
    assertEquals(expected.size(), actual.size());
    assertTrue(actual.containsAll(expected));
  }

  @Test
  public void testLoadInventoryShouldSkipAddingOneProductToDatabaseWhenTwoEansAreTheSame()
      throws DatabaseOperationException {
    // create some test data
    String inventoryId = "testInventory";
    List<ProductNoSql> expected = Arrays.asList(
        ProductNoSql.builder().id(123L).ean("ean1").sku("sku1").name("Product1").stock(10)
            .inventoryId("testInventory").price("45.0").build(),
        ProductNoSql.builder().id(1234L).ean("ean1").sku("sku1").name("Product1").stock(10)
            .inventoryId("testInventory").price("56.0").build(),
        ProductNoSql.builder().id(123456L).ean("ean2").sku("sku2").name("Product2").stock(20)
            .inventoryId("testInventory").price("57.0").build()
    );
    // call the method under test
    mongoDbBaselinkerStocks.loadInventory(inventoryId, expected);
    // verify that the inventory was saved to MongoDB
    List<ProductNoSql> actual = mongoTemplate.findAll(ProductNoSql.class);
    assertEquals(2, actual.size());
  }

  @Test
  public void testUpdateStocksShouldCheckIfItUpdatesEansInDatabase()
      throws DatabaseOperationException {
    // TEST DATA
    String inventoryId = "testInventory1";
    List<ProductNoSql> expectedInventory1 = Arrays.asList(
        ProductNoSql.builder().id(123L).ean("ean1").sku("sku1").name("Product1").stock(10)
            .inventoryId(inventoryId).price("45.0").build(),
        ProductNoSql.builder().id(123456L).ean("ean2").sku("sku2").name("Product2").stock(20)
            .inventoryId(inventoryId).price("57.0").build(),
        ProductNoSql.builder().id(123456L).ean("ean3").sku("sku2").name("Product2").stock(135)
            .inventoryId(inventoryId).price("57.0").build()
    );

    for (int i = 0; i < expectedInventory1.size(); i++) {
      mongoTemplate.save(expectedInventory1.get(i));
    }
    // call the method under test
    Map<String, Integer> eanStockMapToChange = new HashMap<>();
    eanStockMapToChange.put("ean1", 5);
    eanStockMapToChange.put("ean2", 12);
    eanStockMapToChange.put("unkownEAN", 100); // EAN TO BYPASS
    List<ProductNoSql> updatedProducts = mongoDbBaselinkerStocks.updateStocks(inventoryId,
        eanStockMapToChange);

    // verify that the stocks were updated in MongoDB
    Query query = Query.query(Criteria.where("inventoryId").is(inventoryId));
    List<ProductNoSql> allProductsFromDatabaseAfterUpdate = mongoTemplate.find(query,
        ProductNoSql.class);

    assertNotNull(updatedProducts);
    assertEquals(5, allProductsFromDatabaseAfterUpdate.get(0).getStock());
    assertEquals(12, allProductsFromDatabaseAfterUpdate.get(1).getStock());
    assertEquals(100, allProductsFromDatabaseAfterUpdate.get(2).getStock());
    assertEquals(3, allProductsFromDatabaseAfterUpdate.size());
    assertEquals(2, updatedProducts.size());
  }
}
