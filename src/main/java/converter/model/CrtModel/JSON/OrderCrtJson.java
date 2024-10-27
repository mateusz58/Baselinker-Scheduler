package converter.model.CrtModel.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("orders")
public class OrderCrtJson {

  @JsonProperty("events")
  private List<EventCrtJson> events = new LinkedList<>();
  @JsonProperty("state")
  private StateOrderEnum state;
  @JsonProperty("order_number")
  private String orderNumber;
  @JsonProperty("order_id")
  private String orderId;
  @JsonProperty("items")
  private List<Item> items = new LinkedList<>();
  @JsonProperty("store_id")
  private String storeId;
  @JsonProperty("last_event_time")
  private LocalDateTime lastEventTime;
  @JsonProperty("delivery_details")
  private DeliveryDetails deliveryDetails;
  @JsonProperty("customer_billing_address")
  private Address customerBillingAddress;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OrderCrtJson)) {
      return false;
    }
    OrderCrtJson that = (OrderCrtJson) o;
    return getEvents().equals(that.getEvents()) && getState() == that.getState()
        && getOrderNumber().equals(that.getOrderNumber()) && getOrderId().equals(that.getOrderId())
        && getItems().equals(that.getItems()) && getStoreId().equals(that.getStoreId())
        && getLastEventTime().equals(that.getLastEventTime()) && Objects.equals(
        getDeliveryDetails(), that.getDeliveryDetails()) && Objects.equals(
        getCustomerBillingAddress(), that.getCustomerBillingAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getEvents(), getState(), getOrderNumber(), getOrderId(), getItems(),
        getStoreId(), getLastEventTime(), getDeliveryDetails(), getCustomerBillingAddress());
  }
}
