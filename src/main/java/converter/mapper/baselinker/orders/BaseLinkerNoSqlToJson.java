package converter.mapper.baselinker.orders;

import converter.mapper.Mapper;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.RowJson;
import converter.model.baseLinkerModel.NoSql.ItemNoSql;
import converter.model.baseLinkerModel.NoSql.OrderNosql;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseLinkerNoSqlToJson implements Mapper<OrderNosql, OrderBaseLinkerJson> {

  private static final Logger log
      = LoggerFactory.getLogger(BaseLinkerNoSqlToJson.class);

  private List<RowJson> mapItemNoSql(List<ItemNoSql> itemNoSqlList) {
    List<RowJson> rowsJsons = new LinkedList<>();
    for (ItemNoSql itemNoSql : itemNoSqlList) {
      rowsJsons.add(mapItem(itemNoSql));
    }
    return rowsJsons;
  }

  private RowJson mapItem(ItemNoSql itemNoSql) {
    if (itemNoSql == null) {
      log.error("ItemNoSql cannot be null");
      throw new IllegalArgumentException("ItemNoSql cannot be null");
    }
    return RowJson.builder()
        .name(itemNoSql.getName())
        .auctionId(itemNoSql.getAuctionId())
        .productsEan(itemNoSql.getProductsEan())
        .quantity(itemNoSql.getQuantity())
        .symkar(itemNoSql.getSymkar())
        .itemPriceBrutto(itemNoSql.getItemPriceBrutto())
        .productsId(itemNoSql.getProductsId())
        .productsSku(itemNoSql.getProductsSku())
        .build();
  }

  @Override
  public OrderBaseLinkerJson map(OrderNosql object) throws ParseException {
    if (object == null) {
      log.error("Object to map cannot be null");
      throw new IllegalArgumentException("Object to map cannot be null");
    }
    String orderdIdWithoutBL = object.getOrderId().replace("BL", "");
    return OrderBaseLinkerJson.builder()
        .orderId(orderdIdWithoutBL)
        .dateAddGmtUnixTimestamp(object.getTimestampGmt())
        .orderSource(object.getOrderSource())
        .address(object.getAddress())
        .addressCompany(object.getAddressCompany())
        .addressCountryCode(object.getAddressCountryCode())
        .addressName(object.getAddressName())
        .addressCity(object.getAddressCity())
        .address(object.getAddress())
        .addressPostal(object.getAddressPostal())
        .clientName(object.getClientName())
        .clientNip(object.getClientNip())
        .clientAddress(object.getClientAddress())
        .clientCompany(object.getClientCompany())
        .clientCity(object.getClientCity())
        .clientPostal(object.getClientPostal())
        .clientLogin(object.getClientLogin())
        .deliveryType(object.getDeliveryType())
        .deliveryPrice(object.getDeliveryPrice())
        .paymentPaid(object.getPaymentPaid())
        .paymentStatus(object.getPaymentStatus())
        .orderPage(object.getOrderPage())
        .orderAmountBrutto(object.getOrderAmountBrutto())
        .rows(mapItemNoSql(object.getItemNoSqlList()))
        .build();
  }

  @Override
  public List<OrderBaseLinkerJson> map(List<OrderNosql> object) throws ParseException {
    if (object == null) {
      log.error("Object to map cannot be null");
      throw new IllegalArgumentException("Object to map cannot be null");
    }
    List<OrderBaseLinkerJson> list = new LinkedList<>();
    for (int i = 0; i < object.size(); i++) {
      list.add(map(object.get(i)));
    }
    return list;
  }
}
