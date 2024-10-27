package converter.database;

import ch.qos.logback.classic.Logger;
import converter.exceptions.DatabaseOperationException;
import converter.model.stocks.ProductNoSql;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "stocks.database.enabled", havingValue = "true", matchIfMissing = true)
public class MongoDbBaselinkerStocks implements DatabaseStocks<ProductNoSql> {

  private static final Logger log = (Logger) LoggerFactory.getLogger(MongoDbBaselinkerStocks.class);
  MongoTemplate mongoTemplate;

  public MongoDbBaselinkerStocks(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void loadInventory(String inventoryId, List<ProductNoSql> productNoSqls)
      throws DatabaseOperationException {
    try {
      if (inventoryId == null || inventoryId.isEmpty()) {
        log.error("Provided invalid argument for inventoryId", inventoryId);
        throw new IllegalArgumentException("Provided null for inventory");
      }

      int count = 0;
      List<BulkOperations> bulkOperationsList = new ArrayList<>();
      for (ProductNoSql productNoSql : productNoSqls) {
        Query query = Query.query(
            Criteria.where("inventoryId").is(inventoryId).and("ean").is(productNoSql.getEan()));
        Update update = new Update();
        update.set("id", productNoSql.getId());
        update.set("name", productNoSql.getName());
        update.set("inventoryId", inventoryId);
        update.set("stock", productNoSql.getStock());
        update.set("name", productNoSql.getName());
        update.set("price", productNoSql.getName());
        update.set("sku", productNoSql.getSku());
        update.set("ean", productNoSql.getEan());

        count++;
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.ORDERED, "products");
        bulkOperations.upsert(query, update);
        bulkOperationsList.add(bulkOperations);
      }
      bulkOperationsList.forEach(BulkOperations::execute);
      if (count != 0) {
        log.info("Successfully updated or inserted new eans count {} in inventory {}", count,
            inventoryId);
      } else {
        log.info("No new eans loaded from inventory {}", inventoryId);
      }
    } catch (Exception e) {
      String message = "An error occurred during updating stocks in inventory.";
      log.error(message, e.getMessage());
      throw new DatabaseOperationException(message, e);
    }
  }

  @Override
  public List<ProductNoSql> updateStocks(String inventoryId, Map<String, Integer> eanStockMap)
      throws DatabaseOperationException {
    try {
      if (inventoryId == null || inventoryId == "") {
        log.error("Provided null or empty argument for inventory Id");
        throw new IllegalArgumentException("Provided null or empty argument for inventory Id");
      }
      Query query = Query.query(
          Criteria.where("inventoryId").is(inventoryId).and("ean").in(eanStockMap.keySet()));
      List<ProductNoSql> productsToUpdateStocks = mongoTemplate.find(query, ProductNoSql.class);
      List<ProductNoSql> updatedProducts = new LinkedList<>();

      for (ProductNoSql productToUpdate : productsToUpdateStocks) {
        Query findByEan = Query.query(
            Criteria.where("inventoryId").is(inventoryId).and("ean").is(productToUpdate.getEan()));
        Update updater = new Update();
        int oldStock = productToUpdate.getStock();
        int updatedStock = eanStockMap.get(productToUpdate.getEan());
        if (oldStock != updatedStock) {
          updater.set("stock", updatedStock);
          if (mongoTemplate.updateFirst(findByEan, updater, ProductNoSql.class).wasAcknowledged()) {
            log.debug(
                "Product with id {} EAN {} in inventory {} updated from stock {} to {} in DATABASE",
                productToUpdate.getId(), productToUpdate.getEan(), inventoryId, oldStock,
                updatedStock);
            updatedProducts.add(productToUpdate.toBuilder().stock(updatedStock).build());
          }
        }
      }
      return updatedProducts;
    } catch (Exception e) {
      String message = "An error occurred during updating stocks in inventory.";
      log.error(message, e.getMessage());
      throw new DatabaseOperationException(message, e);
    }
  }
}
