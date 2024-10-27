package converter.mapper.baselinker.stocks;

import converter.mapper.Mapper;
import converter.model.baseLinkerModel.JSON.stocks.ProductJson;
import converter.model.baseLinkerModel.JSON.stocks.ProductsJson;
import converter.model.stocks.ProductNoSql;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaseLinkerJsonToBaseLinkerNosqlStocks implements Mapper<ProductsJson, ProductNoSql> {

  @Override
  public ProductNoSql map(ProductsJson object) throws ParseException {
    ProductNoSql productNoSqls = null;
    Map<String, ProductJson> products = object.getProducts();
    for (Map.Entry<String, ProductJson> entry : products.entrySet()) {
      ProductJson productJson = entry.getValue();
      productNoSqls = ProductNoSql.builder()
          .id(productJson.getId())
          .ean(productJson.getEan())
          .sku(productJson.getSku())
          .name(productJson.getName())
          .stock(productJson.getStock().entrySet().stream().findFirst().get().getValue())
          .price(productJson.getPrices().entrySet().stream().findFirst().get().getValue())
          .build();
    }
    return productNoSqls;
  }

  @Override
  public List<ProductNoSql> map(List<ProductsJson> object) throws ParseException {
    List<ProductNoSql> productNoSqls = new LinkedList<>();
    for (int i = 0; i < object.size(); i++) {
      Map<String, ProductJson> products = object.get(i).getProducts();
      for (Map.Entry<String, ProductJson> entry : products.entrySet()) {
        ProductJson productJson = entry.getValue();
        ProductNoSql productNoSql = ProductNoSql.builder()
            .id(productJson.getId())
            .ean(productJson.getEan())
            .sku(productJson.getSku())
            .name(productJson.getName())
            .stock(productJson.getStock().entrySet().stream().findFirst().get().getValue())
            .price(productJson.getPrices().entrySet().stream().findFirst().get().getValue())
            .build();
        productNoSqls.add(productNoSql);
      }
    }
    return productNoSqls;
  }
}
