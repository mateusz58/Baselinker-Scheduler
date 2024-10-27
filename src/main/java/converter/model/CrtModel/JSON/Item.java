package converter.model.CrtModel.JSON;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonPropertyOrder({
    "item_id",
    "ean",
    "price",
    "currency",
    "article_number",
    "zalando_article_number",
    "article_location",
    "cancellation_reason",
    "returnReasonCode",
    "return_location"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {

  @JsonProperty("item_id")
  private String itemId;
  @JsonProperty("ean")
  private String ean;
  @JsonProperty("price")
  private BigDecimal price;
  @JsonProperty("currency")
  private String currency;
  @JsonProperty("article_number")
  private String articleNumber;
  @JsonProperty("zalando_article_number")
  private String zalandoArticleNumber;
  @JsonProperty("article_location")
  private String articleLocation;
  @JsonProperty("cancellation_reason")
  private CancellationReasonEnum cancellationReason;
  @JsonProperty("return_reason_code")
  private Integer returnReasonCode;
  @JsonProperty("return_location")
  private ReturnedItemLocationEnum returnLocation;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Item)) {
      return false;
    }
    Item item = (Item) o;

    return getItemId().equals(item.getItemId()) &&
        getEan().equals(item.getEan()) &&
        getPrice().equals(item.getPrice()) &&
        getCurrency().equals(item.getCurrency()) &&
        getArticleNumber().equals(item.getArticleNumber()) &&
        // Optional fields
        Objects.equals(getArticleLocation(), item.getArticleLocation()) &&
        Objects.equals(getCancellationReason(), item.getCancellationReason()) &&
        Objects.equals(getZalandoArticleNumber(), item.getZalandoArticleNumber()) &&
        Objects.equals(getArticleLocation(), item.getArticleLocation()) &&
        Objects.equals(getReturnReasonCode(), item.getReturnReasonCode())
        && getReturnLocation() == item.getReturnLocation();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getItemId(), getEan(), getPrice(), getCurrency(), getArticleNumber(),
        getZalandoArticleNumber(), getArticleLocation(), getCancellationReason(),
        getReturnReasonCode(), getReturnLocation());
  }
}
