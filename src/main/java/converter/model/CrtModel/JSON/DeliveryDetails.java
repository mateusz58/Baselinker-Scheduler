package converter.model.CrtModel.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDetails {

  @JsonProperty("delivery_tracking_number")
  private String deliveryTrackingNumber;
  @JsonProperty("delivery_carrier_name")
  private String deliveryCarrierName;
  @JsonProperty("return_tracking_number")
  private String returnTrackingNumber;
  @JsonProperty("return_carrier_name")
  private String returnCarrierName;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryDetails)) {
      return false;
    }
    DeliveryDetails that = (DeliveryDetails) o;
    return getDeliveryTrackingNumber().equals(that.getDeliveryTrackingNumber())
        && getDeliveryCarrierName().equals(that.getDeliveryCarrierName())
        && getReturnTrackingNumber().equals(that.getReturnTrackingNumber())
        && getReturnCarrierName().equals(that.getReturnCarrierName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDeliveryTrackingNumber(), getDeliveryCarrierName(),
        getReturnTrackingNumber(), getReturnCarrierName());
  }
}
