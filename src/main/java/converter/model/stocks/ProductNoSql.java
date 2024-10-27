package converter.model.stocks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Getter
@Document(collection = "products")
public class ProductNoSql {

  @Field("id")
  @Indexed(unique = true)
  private final Long id;
  @Field("name")
  private final String name;
  @Field("stock")
  private final Integer stock;
  @Field("price")
  private final String price;
  @Field("inventoryId")
  private final String inventoryId;
  @MongoId
  @JsonIgnore
  String mongoId;
  @Field("sku")
  private String sku;
  @Field("ean")
  @Indexed(unique = true)
  private String ean;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProductNoSql that)) {
      return false;
    }
    return Objects.equals(stock, that.stock) // add null check for stock field
        && Objects.equals(id, that.id) && Objects.equals(
        name, that.name) && Objects.equals(sku, that.sku) && Objects.equals(ean,
        that.ean);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, ean);
  }
}
