package converter.mapper.crt;

import converter.mapper.Mapper;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.CrtModel.NoSql.EventCrtNoSql;
import converter.model.CrtModel.NoSql.OrderCrtNoSql;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class CrtNoSqlToJson implements Mapper<OrderCrtNoSql, OrderCrtJson> {

  private EventCrtJson mapEvent(EventCrtNoSql eventCrtNoSql, OrderCrtNoSql orderCrtNoSql) {
    return EventCrtJson.builder()
        .orderId(orderCrtNoSql.getOrderId())
        .orderNumber(orderCrtNoSql.getOrderNumber())
        .storeId(eventCrtNoSql.getStoreId())
        .deliveryDetails(eventCrtNoSql.getDeliveryDetails())
        .eventId(eventCrtNoSql.getEventId())
        .items(eventCrtNoSql.getItems())
        .cancelledItems(eventCrtNoSql.getCancelledItems())
        .timestamp(eventCrtNoSql.getTimestamp())
        .state(eventCrtNoSql.getState())
        .customerBillingAddress(eventCrtNoSql.getCustomerBillingAddress())
        .build();
  }

  private List<EventCrtJson> mapEvents(OrderCrtNoSql orderCrtNoSql) {
    List<EventCrtJson> events = new LinkedList<>();
    for (int i = 0; i < orderCrtNoSql.getEventCrtNoSqlList().size(); i++) {
      events.add(mapEvent(orderCrtNoSql.getEventCrtNoSqlList().get(i), orderCrtNoSql));
    }
    return events;
  }

  @Override
  public OrderCrtJson map(OrderCrtNoSql object) throws ParseException {
    return OrderCrtJson.builder()
        .items(object.getItems())
        .orderId(object.getOrderId())
        .orderNumber(object.getOrderNumber())
        .state(object.getState())
        .events(mapEvents(object))
        .build();
  }

  @Override
  public List<OrderCrtJson> map(List<OrderCrtNoSql> object) throws ParseException {
    List<OrderCrtJson> orderCrtJsonList = new LinkedList<>();
    for (int i = 0; i < object.size(); i++) {
      orderCrtJsonList.add(map(object.get(i)));
    }
    return orderCrtJsonList;
  }
}
