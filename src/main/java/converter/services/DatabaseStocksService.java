package converter.services;

import converter.database.DatabaseStocks;
import converter.exceptions.DatabaseOperationException;
import converter.exceptions.ServiceOperationException;
import converter.mapper.Mapper;
import converter.mapper.ObjectTranslator;
import converter.model.baseLinkerModel.JSON.stocks.ProductsJson;
import converter.model.stocks.ProductNoSql;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseStocksService implements StocksServiceInterface {

  protected Logger log = LoggerFactory.getLogger(getClass());
  Mapper<ProductsJson, ProductNoSql> mapperJsonNoSql;
  DatabaseStocks<ProductNoSql> databaseStocks;

  public DatabaseStocksService(DatabaseStocks<ProductNoSql> databaseStocks,
      Mapper<ProductsJson, ProductNoSql> mapperJsonNoSql) {
    this.databaseStocks = databaseStocks;
    this.mapperJsonNoSql = mapperJsonNoSql;
  }

  @Override
  public void loadInventory(String inventoryId, ProductsJson objectsJson)
      throws ServiceOperationException {
    if (inventoryId == null) {
      log.error("inventory ids are null");
      throw new IllegalArgumentException("Inventory ids are null");
    }
    try {
      List<ProductNoSql> objectsMappedToDatabaseEntity = Optional.of(
          ObjectTranslator.translate(mapperJsonNoSql, List.of(objectsJson))).orElseThrow(
          () -> new ServiceOperationException("Error while mapping inventory to database"));
      databaseStocks.loadInventory(inventoryId, objectsMappedToDatabaseEntity);
    } catch (ParseException | DatabaseOperationException e) {
      log.error("Error occured", e);
      throw new ServiceOperationException(e);
    }
  }

  @Override
  public Map<LinkedHashSet<String>, Integer> updateStocksInDatabaseReturnHashmapProductIdAndStocks(
      String inventoryId, Map<String, Integer> eanIdsStocks) throws DatabaseOperationException {
    List<ProductNoSql> productNoSqlList = databaseStocks.updateStocks(inventoryId, eanIdsStocks);
    return mapProductsNoSqlToSetOfProuctIdEansToAndStockValueHashMap(productNoSqlList);
  }

  private Map<LinkedHashSet<String>, Integer> mapProductsNoSqlToSetOfProuctIdEansToAndStockValueHashMap(
      List<ProductNoSql> productNoSqls) {
    Map<LinkedHashSet<String>, Integer> mapProductIdEansStocks = new HashMap<>();
    for (ProductNoSql productNoSql : productNoSqls) {
      LinkedHashSet<String> keysSet = new LinkedHashSet<>();
      keysSet.add(productNoSql.getEan());
      keysSet.add(String.valueOf(productNoSql.getId()));
      int value = productNoSql.getStock();
      mapProductIdEansStocks.put(keysSet, value);
    }
    return mapProductIdEansStocks;
  }
}
