package converter.model.CrtModel.JSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Data
@JsonPropertyOrder({
    "event_id",
    "order_id",
    "order_number",
    "state",
    "store_id",
    "timestamp",
    "items",
    "cancelled_items",
    "customer_billing_address"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventCrtJson {

  @JsonProperty("event_id")
  private String eventId;
  @JsonProperty("order_id")
  private String orderId;
  @JsonProperty("order_number")
  private String orderNumber;
  @JsonProperty("state")
  private StateOrderEnum state;
  @JsonProperty("store_id")
  private String storeId;
  @JsonProperty("timestamp")
  private LocalDateTime timestamp;
  @JsonProperty("delivery_details")
  private DeliveryDetails deliveryDetails;
  @JsonProperty("customer_billing_address")
  private Address customerBillingAddress;
  @JsonProperty("items")
  private List<Item> items;
  @JsonProperty("cancelled_items")
  private List<Item> cancelledItems;
}
