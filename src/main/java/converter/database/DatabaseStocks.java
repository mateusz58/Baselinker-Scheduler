package converter.database;

import converter.exceptions.DatabaseOperationException;
import converter.model.stocks.ProductNoSql;
import java.util.List;
import java.util.Map;

public interface DatabaseStocks<X> {

  void loadInventory(String inventoryId, List<ProductNoSql> productNoSqlList)
      throws DatabaseOperationException;

  List<ProductNoSql> updateStocks(String inventoryId, Map<String, Integer> eanStockMap)
      throws DatabaseOperationException;
}
