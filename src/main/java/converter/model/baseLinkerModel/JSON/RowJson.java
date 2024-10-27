package converter.model.baseLinkerModel.JSON;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
    "name",
    "products_id",
    "products_ean",
    "products_sku",
    "auction_id",
    "item_price_brutto",
    "quantity",
    "amount_brutto",
    "vat_rate",
    "symkar",
    "date_add"
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RowJson {

  @JsonProperty("name")
  private String name;
  @JsonAlias({"product_id"})
  @JsonProperty("products_id")
  private String productsId;
  @JsonAlias({"ean", "products_ean"})
  @JsonProperty("products_ean")
  private String productsEan;
  @JsonAlias("sku")
  @JsonProperty("products_sku")
  private String productsSku;
  @JsonAlias({"tax_rate"})
  @JsonProperty("vat_rate")
  private BigDecimal vatRate;
  @JsonProperty("symkar")
  private String symkar;
  @JsonAlias({"quantity"})
  private Integer quantity;
  @JsonAlias({"price_brutto"})
  @JsonProperty("item_price_brutto")
  private BigDecimal itemPriceBrutto;
  @JsonAlias({"auction_id"})
  @JsonProperty("auction_id")
  private String auctionId;
  @JsonUnwrapped
  @JsonIgnore
  private RowReadIgnoredFieldsJson rowReadOnlyFields;

  public void setSymkar() {
    this.symkar = this.productsId;
  }
}
