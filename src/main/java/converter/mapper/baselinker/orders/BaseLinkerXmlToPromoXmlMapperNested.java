package converter.mapper.baselinker.orders;

import converter.helper.NameFormatter;
import converter.helper.StringProcessor;
import converter.helper.baseLinker.DateTimeFormattersBaseLinkerPromo;
import converter.mapper.Mapper;
import converter.model.baseLinkerModel.XML.OrderBaseLinkerXml;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.model.baseLinkerModel.XML.RowXml;
import converter.model.promoModel.ItemPromo;
import converter.model.promoModel.ItemsPromo;
import converter.model.promoModel.OrderData;
import converter.model.promoModel.OrderPromo;
import converter.model.promoModel.OrdersPromo;
import converter.model.promoModel.Payment;
import converter.model.promoModel.SellTo;
import converter.model.promoModel.ShipTo;
import converter.model.promoModel.Shipment;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BaseLinkerXmlToPromoXmlMapperNested implements
    Mapper<OrdersBaseLinkerXml, OrdersPromo> {

  private final static ZoneId zoneToChange = ZoneId.of("Europe/London");

  private List<ItemPromo> itemsList(List<RowXml> rows, String dateCreated) throws ParseException {
    List<ItemPromo> list = new LinkedList<>();
    for (int i = 0; i < rows.size(); i++) {
      list.add(ItemPromo.builder()
          .itemPrice(rows.get(i).getItemPriceBrutto())
          .quantity(rows.get(i).getQuantity())
          .billingText(rows.get(i).getName())
          .tbId(rows.get(i).getAuctionId())
          .ean(rows.get(i).getProductsEan())
          .sku(rows.get(i).getProductsEan())
          .channelId("")
          .channelSku("")
          .dateCreated(DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToPromoDateTime(
              dateCreated, zoneToChange))
          .build());
    }
    return list;
  }

  private int calculateItemCount(List<RowXml> itemList) {
    int count = 0;
    for (int i = 0; i < itemList.size(); i++) {
      count += itemList.get(i).getQuantity();
    }
    return count;
  }

  public OrdersPromo map(OrdersBaseLinkerXml ordersBaseLinker) throws ParseException {
    return OrdersPromo.builder()
        .orders(mapList(ordersBaseLinker.getOrders()))
        .build();
  }

  public List<OrdersPromo> map(List<OrdersBaseLinkerXml> ordersBaseLinker) throws ParseException {
    List<OrdersPromo> list = new LinkedList<>();
    for (int i = 0; i < ordersBaseLinker.size(); i++) {
      list.add(map(ordersBaseLinker.get(i)));
    }
    return list;
  }

  private OrderPromo mapSingleObject(OrderBaseLinkerXml orderBaseLinker) throws ParseException {
    return OrderPromo.builder()
        .orderData(OrderData.builder()
            .channelSign(
                Optional.ofNullable(orderBaseLinker.getOrderSource()).orElseGet(() -> "allegro")
                    + "pl")
            .channelNo("")
            .orderDate(
                DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToDateTimeBaseLinkerWithDifferentTimeZone(
                    orderBaseLinker.getDateAdd(), zoneToChange))
            .itemCount(calculateItemCount(orderBaseLinker.getRows().getRow()))
            .paid(orderBaseLinker.getPaymentPaid().setScale(2))
            .dateCreated(DateTimeFormattersBaseLinkerPromo.convertDateTimeBaseLinkerToPromoDateTime(
                orderBaseLinker.getDateAdd(), zoneToChange))
            .approved(orderBaseLinker.getPaymentStatus())
            .totalItemAmount(orderBaseLinker.getOrderAmountBrutto())
            .tbIdOrderData("BL" + orderBaseLinker.getOrderId())
            .build())
        .sellTo(SellTo.builder()
            .tbISellTo(orderBaseLinker.getClientEmail())
            .channelNo("")
            .firstname(StringProcessor.replacePolishCharacters(
                NameFormatter.getFirstName(orderBaseLinker.getClientName())))
            .lastname(StringProcessor.replacePolishCharacters(
                NameFormatter.getLastName(orderBaseLinker.getClientName())))
            .name(StringProcessor.replacePolishCharacters(orderBaseLinker.getClientName()))
            .streetNo(StringProcessor.removePreffixStreetName(
                StringProcessor.replacePolishCharacters(orderBaseLinker.getAddress())))
            .zip(orderBaseLinker.getClientPostal())
            .city(StringProcessor.replacePolishCharacters(orderBaseLinker.getClientCity()))
            .country(orderBaseLinker.getAddressCountryCode())
            .email(orderBaseLinker.getClientEmail()).build())
        .shipTo(ShipTo.builder()
            .tbId(orderBaseLinker.getClientEmail())
            .channelNo("")
            .firstname(StringProcessor.replacePolishCharacters(
                NameFormatter.getFirstName(orderBaseLinker.getAddressName())))
            .lastname(StringProcessor.replacePolishCharacters(
                NameFormatter.getLastName(orderBaseLinker.getAddressName())))
            .name(StringProcessor.replacePolishCharacters(orderBaseLinker.getAddressName()))
            .streetNo(StringProcessor.removePreffixStreetName(
                StringProcessor.replacePolishCharacters(orderBaseLinker.getAddress())))
            .zip(orderBaseLinker.getAddressPostal())
            .city(StringProcessor.replacePolishCharacters(orderBaseLinker.getAddressCity()))
            .country(orderBaseLinker.getAddressCountryCode())
            .email(orderBaseLinker.getClientEmail()).build())
        .shipment(Shipment.builder()
            .price(BigDecimal.ZERO)
            .build())
        .payment(Payment.builder()
            .type(Optional.ofNullable(orderBaseLinker.getOrderSource()).orElseGet(() -> "allegro")
                + "pl")
            .costs(BigDecimal.ZERO)
            .directdebit("")
            .build())
        .itemsPromo(
            ItemsPromo.builder()
                .itemPromo(
                    itemsList(orderBaseLinker.getRows().getRow(), orderBaseLinker.getDateAdd()))
                .build())
        .build();
  }

  private List<OrderPromo> mapList(List<OrderBaseLinkerXml> orderBaseLinker) throws ParseException {
    List<OrderPromo> orderPromos = new LinkedList<>();
    for (int i = 0; i < orderBaseLinker.size(); i++) {
      orderPromos.add(mapSingleObject(orderBaseLinker.get(i)));
    }
    return orderPromos;
  }
}
