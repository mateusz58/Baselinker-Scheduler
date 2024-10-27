package converter.model.baseLinkerModel.JSON.stocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ProductsJson {

  @JsonProperty("products")
  private Map<String, ProductJson> products;
}