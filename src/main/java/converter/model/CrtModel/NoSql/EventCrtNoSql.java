package converter.model.CrtModel.NoSql;

import converter.model.CrtModel.JSON.Address;
import converter.model.CrtModel.JSON.DeliveryDetails;
import converter.model.CrtModel.JSON.Item;
import converter.model.CrtModel.JSON.StateOrderEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Data
public class EventCrtNoSql implements Comparable {

  @Field("event_id")
  private String eventId;

  @Field("state")
  private StateOrderEnum state;

  @Field("store_id")
  private String storeId;

  @NonNull
  @Field("timestamp")
  private LocalDateTime timestamp;

  @Field("delivery_details")
  private DeliveryDetails deliveryDetails;

  @Field("customer_billing_address")
  private Address customerBillingAddress;

  @Field("items")
  private List<Item> items;

  @Field("cancelled_items")
  private List<Item> cancelledItems;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EventCrtNoSql)) {
      return false;
    }
    EventCrtNoSql that = (EventCrtNoSql) o;
    return getEventId().equals(that.getEventId())
        && getState() == that.getState()
        && getStoreId().equals(that.getStoreId())
        && getTimestamp().equals(that.getTimestamp())
        && Objects.equals(getDeliveryDetails(), that.getDeliveryDetails())
        && Objects.equals(getCustomerBillingAddress(), that.getCustomerBillingAddress())
        && getItems().equals(that.getItems()) &&
        Objects.equals(getCancelledItems(), that.getCancelledItems());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getEventId(), getState(), getStoreId(), getTimestamp(),
        getDeliveryDetails(), getCustomerBillingAddress(), getItems(), getCancelledItems());
  }

  @Override
  public int compareTo(Object o) {
    EventCrtNoSql that = (EventCrtNoSql) o;
    return getTimestamp().compareTo(that.getTimestamp());
  }
}
