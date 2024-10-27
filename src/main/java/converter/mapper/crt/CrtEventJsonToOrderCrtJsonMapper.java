package converter.mapper.crt;

import converter.mapper.Mapper;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CrtEventJsonToOrderCrtJsonMapper implements Mapper<EventCrtJson, OrderCrtJson> {

  @Override
  public List<OrderCrtJson> map(List<EventCrtJson> eventCrtJsonList) throws ParseException {
    List<OrderCrtJson> orderCrtJsonList = new LinkedList<>();
    for (EventCrtJson event : eventCrtJsonList) {
      orderCrtJsonList.add(map(event));
    }
    return orderCrtJsonList;
  }

  public OrderCrtJson map(EventCrtJson event) {
    return OrderCrtJson.builder()
        .orderNumber(event.getOrderNumber())
        .state(event.getState())
        .orderId(event.getOrderId())
        .events(List.of(event))
        .items(event.getItems())
        .customerBillingAddress(Optional.ofNullable(event.getCustomerBillingAddress()).orElse(null))
        .deliveryDetails(event.getDeliveryDetails())
        .lastEventTime(event.getTimestamp())
        .storeId(event.getStoreId())
        .build();
  }
}
