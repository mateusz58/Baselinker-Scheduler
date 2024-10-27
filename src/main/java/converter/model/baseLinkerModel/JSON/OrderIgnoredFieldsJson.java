package converter.model.baseLinkerModel.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class OrderIgnoredFieldsJson {

  @JsonProperty(value = "seller_comments")
  private String sellerComments;
  @JsonProperty(value = "order_status_id")
  private String orderStatusId;
  @JsonProperty(value = "currency")
  private String currency;
  @JsonProperty(value = "extra_field_1")
  private String extraField1;
  @JsonProperty(value = "extra_field_2")
  private String extraField2;
  @JsonProperty(value = "order_page")
  private String order_page;
  @JsonProperty(value = "pick_state")
  private String pickState;
  @JsonProperty(value = "pack_state")
  private String packState;
  @JsonProperty(value = "date_add")
  private LocalDateTime dateAdd;
  @JsonProperty(value = "date_in_status")
  private LocalDateTime dateInStatus;
  @JsonProperty(value = "payment_method_cod")
  private LocalDateTime paymentMethodCod;
}
