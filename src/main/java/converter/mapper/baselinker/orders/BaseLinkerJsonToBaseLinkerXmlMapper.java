package converter.mapper.baselinker.orders;

import static converter.helper.DateTimeFunctions.convertUnixTimeStampToLocalDateTime;

import converter.mapper.Mapper;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.JSON.RowJson;
import converter.model.baseLinkerModel.XML.OrderBaseLinkerXml;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.model.baseLinkerModel.XML.RowXml;
import converter.model.baseLinkerModel.XML.RowsXml;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BaseLinkerJsonToBaseLinkerXmlMapper implements
    Mapper<OrdersBaseLinkerJson, OrdersBaseLinkerXml> {

  private String date;

  private List<OrderBaseLinkerXml> mapListOrders(List<OrderBaseLinkerJson> listOrdersBaseLinkerJson)
      throws ParseException {
    List<OrderBaseLinkerXml> ordersBaseLinkerXmls = new LinkedList<>();
    for (int i = 0; i < listOrdersBaseLinkerJson.size(); i++) {
      ordersBaseLinkerXmls.add(mapObject(listOrdersBaseLinkerJson.get(i)));
    }
    return ordersBaseLinkerXmls;
  }

  private RowsXml mapRow(List<RowJson> rowJsons) {
    List<RowXml> rowXmls = new LinkedList<>();
    for (int i = 0; i < rowJsons.size(); i++) {
      BigDecimal itemPriceBrutto = rowJsons.get(i).getItemPriceBrutto();
      int quantity = rowJsons.get(i).getQuantity();
      BigDecimal amountBrutto = itemPriceBrutto.multiply(BigDecimal.valueOf(quantity));
      rowJsons.get(i).setSymkar();
      RowXml row =
          RowXml.builder()
              .name(rowJsons.get(i).getName())
              .productsId(rowJsons.get(i).getProductsId())
              .amountBrutto(amountBrutto)
              .itemPriceBrutto(itemPriceBrutto.setScale(2))
              .productsEan(rowJsons.get(i).getProductsEan())
              .productsSku(rowJsons.get(i).getProductsSku())
              .quantity(quantity)
              .symkar(rowJsons.get(i).getSymkar())
              .vatRate(
                  Optional.ofNullable(rowJsons.get(i).getVatRate()).isPresent() ? rowJsons.get(i)
                      .getVatRate().setScale(2) : BigDecimal.ZERO)
              .auctionId(rowJsons.get(i).getAuctionId())
              .dateAdd("")
              //.dateAdd(date)
              .build();
      rowXmls.add(row);
    }
    return RowsXml.builder()
        .row(rowXmls)
        .build();
  }

  private BigDecimal calculateOrderAmountBrutto(List<RowJson> items, BigDecimal deliveryPrice) {
    double result = 0;
    for (int i = 0; i < items.size(); i++) {
      result +=
          items.get(i).getQuantity().longValue() * items.get(i).getItemPriceBrutto().longValue();
    }
    return BigDecimal.valueOf(result);
  }

  private OrderBaseLinkerXml mapObject(OrderBaseLinkerJson orderBaseLinkerJson)
      throws ParseException {
    orderBaseLinkerJson.setAddressPni();
    LocalDateTime localDateTime = convertUnixTimeStampToLocalDateTime(
        orderBaseLinkerJson.getDateAddGmtUnixTimestamp(), ZoneId.of("Europe/Warsaw"));
    this.date = String.format("%02d.%02d.%02d %02d:%02d:%02d", localDateTime.getDayOfMonth(),
        localDateTime.getMonthValue(), localDateTime.getYear(), localDateTime.getHour(),
        localDateTime.getMinute(), localDateTime.getSecond());
    BigDecimal deliveryPrice = orderBaseLinkerJson.getDeliveryPrice();
    return OrderBaseLinkerXml.builder()
        .orderId(String.valueOf(orderBaseLinkerJson.getOrderId()))
        .dateAdd(date)
        .paymentName(orderBaseLinkerJson.getPaymentName())
        .paymentStatus((orderBaseLinkerJson.getPaymentStatus()) ? 1 : 0)
        .paymentPaid(orderBaseLinkerJson.getPaymentPaid())
        .deliveryType(orderBaseLinkerJson.getDeliveryType())
        .deliveryPrice(deliveryPrice)

        .buyerComments("")
        .sellerComments("")

        .clientPhone(orderBaseLinkerJson.getClientPhone())
        .clientLogin(orderBaseLinkerJson.getClientLogin())
        .clientEmail(orderBaseLinkerJson.getClientEmail())
        .clientName(orderBaseLinkerJson.getClientName())
        .clientCompany(orderBaseLinkerJson.getClientCompany())
        .clientNip(orderBaseLinkerJson.getClientNip())
        .clientPostal(orderBaseLinkerJson.getClientPostal())
        .clientCity(orderBaseLinkerJson.getClientCity())
        .clientAddress(orderBaseLinkerJson.getClientAddress())
        .clientWantInvoice(
            Optional.ofNullable(orderBaseLinkerJson.getClientWantInvoice()).isPresent()
                ? Integer.parseInt(orderBaseLinkerJson.getClientWantInvoice()) : 0)
        .address(orderBaseLinkerJson.getAddress())
        .addressName(orderBaseLinkerJson.getAddressName())
        .addressCompany(orderBaseLinkerJson.getAddressCompany())
        .addressCountry(orderBaseLinkerJson.getAddressCountry())
        .addressCountryCode(orderBaseLinkerJson.getAddressCountryCode())
        .addressCity(orderBaseLinkerJson.getAddressCity())
        .addressPostal(orderBaseLinkerJson.getAddressPostal())
        .addressBoxMachine(orderBaseLinkerJson.getAddressBoxMachine())
        .addressPni(orderBaseLinkerJson.getAddressPni())

        .orderAmountBrutto(
            calculateOrderAmountBrutto(orderBaseLinkerJson.getRows(), deliveryPrice).setScale(2))
        .rows(mapRow(orderBaseLinkerJson.getRows()))

        .orderSource(orderBaseLinkerJson.getOrderSource())
        .build();
  }

  @Override
  public OrdersBaseLinkerXml map(OrdersBaseLinkerJson object) throws ParseException {
    return OrdersBaseLinkerXml.builder()
        .orders(mapListOrders(object.getOrders()))
        .build();
  }

  @Override
  public List<OrdersBaseLinkerXml> map(List<OrdersBaseLinkerJson> object) throws ParseException {
    List<OrdersBaseLinkerXml> ordersBaseLinkerXmls = new ArrayList<>();
    for (int i = 0; i < object.size(); i++) {
      ordersBaseLinkerXmls.add(map(object.get(i)));
    }
    return ordersBaseLinkerXmls;
  }
}
