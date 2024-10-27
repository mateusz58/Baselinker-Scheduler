package converter.mapper.crt;

import converter.helper.NameFormatter;
import converter.helper.StringProcessor;
import converter.mapper.Mapper;
import converter.model.CrtModel.CrtCodeShops;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.promoModel.Event;
import converter.model.promoModel.EventType;
import converter.model.promoModel.History;
import converter.model.promoModel.ItemPromo;
import converter.model.promoModel.ItemsPromo;
import converter.model.promoModel.OrderData;
import converter.model.promoModel.OrderPromo;
import converter.model.promoModel.Payment;
import converter.model.promoModel.SellTo;
import converter.model.promoModel.ShipTo;
import converter.model.promoModel.Shipment;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CrtJsonToPromo implements Mapper<OrderCrtJson, OrderPromo> {

  private List<ItemPromo> mapItems(List<converter.model.CrtModel.JSON.Item> items,
      String dateCreated) {
    List<ItemPromo> list = new LinkedList<>();
    for (int i = 0; i < items.size(); i++) {
      list.add(ItemPromo.builder()
          .itemPrice(items.get(i).getPrice())
          .quantity(Integer.valueOf(
              (int) items.stream().filter(s -> s.getItemId().equals(s.getItemId())).count()))
          .billingText(items.get(i).getArticleNumber())
          .tbId(items.get(i).getItemId())
          .transferPrice(items.get(i).getPrice())
          .ean(items.get(i).getEan())
          .sku(items.get(i).getArticleNumber())
          .channelId("0")
          .channelSku(items.get(i).getArticleNumber())
          .dateCreated(dateCreated)
          .build());
    }
    return list;
  }

  private Event mapEvent(EventCrtJson eventCrtJson) {
    return Event.builder()
        .eventId(eventCrtJson.getEventId())
        .eventType(EventType.ORDER_APPROVED.toString())
        .dateCreated(eventCrtJson.getTimestamp().withNano(0).toString()).build();
  }

  private List<Event> mapEventList(List<EventCrtJson> eventCrtJsons) {
    List<Event> eventList = new LinkedList<>();
    for (EventCrtJson eventCrtJson : eventCrtJsons) {
      eventList.add(mapEvent(eventCrtJson));
    }
    return eventList;
  }

  private CrtCodeShops setProperPaymentConditionBasedOnStoreId(String storeId) {
    if (storeId.contains("09")) {
      return CrtCodeShops.ZCRPPT;
    }
    if (storeId.contains("10")) {
      return CrtCodeShops.ZCRPSW;
    }
    if (storeId.contains("11")) {
      return CrtCodeShops.ZCRPMY;
    }
    if (storeId.contains("12")) {
      return CrtCodeShops.ZCRPDM;
    }
    return CrtCodeShops.DEFAULT;
  }

  private String addSuffixToOrderIdBasedStoreId(String storeId) {
    if (storeId != null) {
      if (storeId.contains("09")) {
        return "-09";
      }
      if (storeId.contains("10")) {
        return "-10";
      }
      if (storeId.contains("11")) {
        return "-11";
      }
      if (storeId.contains("12")) {
        return "-12";
      }
    }
    return "";
  }

  @Override
  public OrderPromo map(OrderCrtJson orderCrtJson) throws ParseException {
    return OrderPromo.builder()
        .orderData(OrderData.builder()
            .tbIdOrderData(orderCrtJson.getOrderNumber() + addSuffixToOrderIdBasedStoreId(
                orderCrtJson.getStoreId()))
            .fixDhlIdCrtOnly(Optional.ofNullable(orderCrtJson.getDeliveryDetails()).isPresent()
                ? orderCrtJson.getDeliveryDetails().getDeliveryTrackingNumber() : null)
            .fixDhlRetourIdCrtOnly(
                Optional.ofNullable(orderCrtJson.getDeliveryDetails()).isPresent()
                    ? orderCrtJson.getDeliveryDetails().getReturnTrackingNumber() : null)
            .paid(BigDecimal.valueOf(
                    orderCrtJson.getItems().stream().mapToDouble(s -> s.getPrice().doubleValue()).sum())
                .setScale(2))
            .channelId(orderCrtJson.getOrderNumber() + addSuffixToOrderIdBasedStoreId(
                orderCrtJson.getStoreId()))
            .channelSign(
                setProperPaymentConditionBasedOnStoreId(orderCrtJson.getStoreId()).toString())
            .channelNo(orderCrtJson.getOrderNumber() + addSuffixToOrderIdBasedStoreId(
                orderCrtJson.getStoreId()))
            .orderDate(orderCrtJson.getLastEventTime().toLocalDate().toString())
            .itemCount(orderCrtJson.getItems().size())
            .dateCreated(orderCrtJson.getLastEventTime().toLocalDate().toString())
            .approved(1)
            .totalItemAmount(BigDecimal.valueOf(
                orderCrtJson.getItems().stream().mapToDouble(s -> s.getPrice().doubleValue())
                    .sum()))
            .build())
        .sellTo(SellTo.builder()
            .tbISellTo("")
            .channelNo("")
            .title("")
            .firstname(StringProcessor.replacePolishCharacters(NameFormatter.getFirstName(
                orderCrtJson.getCustomerBillingAddress().getFirstName())))
            .lastname(StringProcessor.replacePolishCharacters(
                NameFormatter.getLastName(orderCrtJson.getCustomerBillingAddress().getLastName())))
            .name(StringProcessor.replacePolishCharacters(
                orderCrtJson.getCustomerBillingAddress().getFirstName() + " "
                    + orderCrtJson.getCustomerBillingAddress().getLastName()))
            .streetNo(StringProcessor.removePreffixStreetName(
                StringProcessor.replacePolishCharacters(
                    orderCrtJson.getCustomerBillingAddress().getAddressLine1())))
            .zip(orderCrtJson.getCustomerBillingAddress().getZipCode())
            .city(StringProcessor.replacePolishCharacters(
                orderCrtJson.getCustomerBillingAddress().getCity()))
            .country(orderCrtJson.getCustomerBillingAddress().getCountryCode())
            .build())
        .shipTo(ShipTo.builder()
            .tbId("")
            .channelNo("")
            .title("")
            .firstname(StringProcessor.replacePolishCharacters(NameFormatter.getFirstName(
                orderCrtJson.getCustomerBillingAddress().getFirstName())))
            .lastname(StringProcessor.replacePolishCharacters(
                NameFormatter.getLastName(orderCrtJson.getCustomerBillingAddress().getLastName())))
            .name(StringProcessor.replacePolishCharacters(
                orderCrtJson.getCustomerBillingAddress().getFirstName() + " "
                    + orderCrtJson.getCustomerBillingAddress().getLastName()))
            .streetNo(StringProcessor.removePreffixStreetName(
                StringProcessor.replacePolishCharacters(
                    orderCrtJson.getCustomerBillingAddress().getAddressLine1())))
            .zip(orderCrtJson.getCustomerBillingAddress().getZipCode())
            .city(StringProcessor.replacePolishCharacters(
                orderCrtJson.getCustomerBillingAddress().getCity()))
            .country(orderCrtJson.getCustomerBillingAddress().getCountryCode())
            .email("")
            .build())
        .shipment(Shipment.builder()
            .price(BigDecimal.ZERO)
            .build())
        .payment(Payment.builder()
            .type(setProperPaymentConditionBasedOnStoreId(orderCrtJson.getStoreId()).toString())
            .costs(BigDecimal.ZERO)
            .directdebit("")
            .build())
        .itemsPromo(
            ItemsPromo.builder()
                .itemPromo(mapItems(orderCrtJson.getItems(),
                    orderCrtJson.getLastEventTime().withNano(0).toString()))
                .build())
        .history(
            History.builder()
                .event(mapEventList(orderCrtJson.getEvents()))
                .build())
        .build();
  }

  @Override
  public List<OrderPromo> map(List<OrderCrtJson> ordersCrtList) throws ParseException {
    List<OrderPromo> orderPromoList = new LinkedList<>();
    for (int i = 0; i < ordersCrtList.size(); i++) {
      orderPromoList.add(map(ordersCrtList.get(i)));
    }
    return orderPromoList;
  }
}

