package converter.mapper.baselinker.orders;

import converter.helper.DateTimeFunctions;
import converter.mapper.Mapper;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.RowJson;
import converter.model.baseLinkerModel.NoSql.ItemNoSql;
import converter.model.baseLinkerModel.NoSql.OrderNosql;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BaseLinkerJsonToBaseLinkerNosql implements Mapper<OrderBaseLinkerJson, OrderNosql> {

  private List<ItemNoSql> mapRowsList(List<RowJson> rowJsonList) {
    List<ItemNoSql> itemNoSqls = new LinkedList<>();
    for (RowJson rowJson : rowJsonList) {
      itemNoSqls.add(mapRow(rowJson));
    }
    return itemNoSqls;
  }

  private ItemNoSql mapRow(RowJson rowJson) {
    return ItemNoSql.builder()
        .name(rowJson.getName())
        .auctionId(rowJson.getAuctionId())
        .productsEan(rowJson.getProductsEan())
        .quantity(rowJson.getQuantity())
        .symkar(rowJson.getSymkar())
        .itemPriceBrutto(rowJson.getItemPriceBrutto())
        .productsId(rowJson.getProductsId())
        .productsSku(rowJson.getProductsSku())
        .build();
  }

  private BigDecimal calculateOrderAmountBrutto(List<RowJson> items, BigDecimal deliveryPrice) {
    double result = 0;
    for (int i = 0; i < items.size(); i++) {
      result +=
          items.get(i).getQuantity().longValue() * items.get(i).getItemPriceBrutto().longValue();
    }
    return BigDecimal.valueOf(result).setScale(2);
  }

  @Override
  public OrderNosql map(OrderBaseLinkerJson object) throws ParseException {
    return OrderNosql.builder()
        .orderId("BL" + object.getOrderId())
        .orderDateAdd(DateTimeFunctions.convertUnixTimeStampToLocalDateTime(
            object.getDateAddGmtUnixTimestamp(), ZoneId.of("Europe/Warsaw")))
        .timestampGmt(object.getDateAddGmtUnixTimestamp())
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
        .paymentStatus(Optional.ofNullable(object.getPaymentStatus()).orElse(false))
        .orderPage(object.getOrderPage())
        .orderAmountBrutto(
            String.valueOf(calculateOrderAmountBrutto(object.getRows(), BigDecimal.ZERO)))
        .itemNoSqlList(mapRowsList(object.getRows()))
        .build();
  }

  @Override
  public List<OrderNosql> map(List<OrderBaseLinkerJson> object) throws ParseException {
    List<OrderNosql> list = new LinkedList<>();
    for (int i = 0; i < object.size(); i++) {
      list.add(map(object.get(i)));
    }
    return list;
  }
}
