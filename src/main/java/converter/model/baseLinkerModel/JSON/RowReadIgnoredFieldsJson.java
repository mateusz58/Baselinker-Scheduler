package converter.model.baseLinkerModel.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RowReadIgnoredFieldsJson {

  @JsonProperty("attributes")
  private String attributes;
  @JsonProperty("location")
  private String location;
  @JsonProperty("warehouse_id")
  private String warehouseId;
  @JsonProperty("weight")
  private String weight;
  @JsonProperty("bundle_id")
  private String bundleId;
  @JsonProperty("order_product_id")
  private String orderProductId;
}
