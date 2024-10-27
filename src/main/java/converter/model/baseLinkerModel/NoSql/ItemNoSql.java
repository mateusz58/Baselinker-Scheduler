package converter.model.baseLinkerModel.NoSql;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemNoSql {

  @Field("name")
  private String name;
  @Field("products_id")
  private String productsId;
  @Field("ean")
  private String productsEan;
  @Field("sku")
  private String productsSku;
  @Field("symkar")
  private String symkar;
  @Field("quantity")
  private Integer quantity;
  @Field("item_price_brutto")
  private BigDecimal itemPriceBrutto;
  @Field("auction_id")
  private String auctionId;

  public void setSymkar() {
    this.symkar = this.productsId;
  }
}
