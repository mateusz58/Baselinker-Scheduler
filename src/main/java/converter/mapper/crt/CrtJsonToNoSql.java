package converter.mapper.crt;

import converter.mapper.Mapper;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.CrtModel.NoSql.EventCrtNoSql;
import converter.model.CrtModel.NoSql.OrderCrtNoSql;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class CrtJsonToNoSql implements Mapper<OrderCrtJson, OrderCrtNoSql> {

  @Override
  public OrderCrtNoSql map(OrderCrtJson object) throws ParseException {
    return OrderCrtNoSql.builder()
        .orderId(object.getOrderId())
        .orderNumber(object.getOrderNumber())
        .state(object.getState())
        .items(object.getItems())
        .eventCrtNoSqlList(mapEventList(object.getEvents()))
        .build();
  }

  @Override
  public List<OrderCrtNoSql> map(List<OrderCrtJson> object) throws ParseException {
    List<OrderCrtNoSql> orderCrtNoSqlList = new LinkedList<>();
    for (OrderCrtJson orderCrtJson : object) {
      orderCrtNoSqlList.add(map(orderCrtJson));
    }
    return orderCrtNoSqlList;
  }

  private EventCrtNoSql mapEvent(EventCrtJson eventCrtJson) {
    return EventCrtNoSql.builder()
        .eventId(eventCrtJson.getEventId())
        .state(eventCrtJson.getState())
        .storeId(eventCrtJson.getStoreId())
        .cancelledItems(eventCrtJson.getCancelledItems())
        .customerBillingAddress(eventCrtJson.getCustomerBillingAddress())
        .deliveryDetails(eventCrtJson.getDeliveryDetails())
        .timestamp(eventCrtJson.getTimestamp())
        .items(eventCrtJson.getItems())
        .build();
  }

  private List<EventCrtNoSql> mapEventList(List<EventCrtJson> eventCrtJsonList) {
    List<EventCrtNoSql> eventCrtNoSqlList = new LinkedList<>();
    for (EventCrtJson eventCrtJson : eventCrtJsonList) {
      eventCrtNoSqlList.add(mapEvent(eventCrtJson));
    }
    return eventCrtNoSqlList;
  }
}
