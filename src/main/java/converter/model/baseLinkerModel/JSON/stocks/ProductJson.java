package converter.model.baseLinkerModel.JSON.stocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ProductJson {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("ean")
  private String ean;

  @JsonProperty("sku")
  private String sku;

  @JsonProperty("name")
  private String name;

  @JsonProperty("stock")
  private Map<String, Integer> stock;

  @JsonProperty("prices")
  private Map<String, String> prices;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProductJson that)) {
      return false;
    }
    return getId() == that.getId() && Objects.equals(getEan(), that.getEan())
        && Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getEan(), getName());
  }
}
