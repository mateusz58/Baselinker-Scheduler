package converter.services;

import converter.exceptions.DatabaseOperationException;
import converter.exceptions.ServiceOperationException;
import converter.model.baseLinkerModel.JSON.stocks.ProductsJson;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Map;

public interface StocksServiceInterface {

  void loadInventory(String inventoryId, ProductsJson productsJson)
      throws ParseException, DatabaseOperationException, ServiceOperationException;

  Map<LinkedHashSet<String>, Integer> updateStocksInDatabaseReturnHashmapProductIdAndStocks(
      String inventoryId,
      Map<String, Integer> eanIds)
      throws ParseException, DatabaseOperationException, ServiceOperationException;
}
