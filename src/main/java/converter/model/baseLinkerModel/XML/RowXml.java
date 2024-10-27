package converter.model.baseLinkerModel.XML;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
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
public class RowXml {

  @JacksonXmlProperty(localName = "name")
  private String name;
  @JacksonXmlProperty(localName = "products_id")
  private String productsId;
  @JacksonXmlProperty(localName = "products_ean")
  private String productsEan;
  @JacksonXmlProperty(localName = "products_sku")
  private String productsSku;
  @JacksonXmlProperty(localName = "vat_rate")
  private BigDecimal vatRate;
  @JacksonXmlProperty(localName = "symkar")
  private String symkar;
  @JacksonXmlProperty(localName = "amount_brutto")
  private BigDecimal amountBrutto;
  @JacksonXmlProperty(localName = "quantity")
  private Integer quantity;
  @JacksonXmlProperty(localName = "item_price_brutto")
  private BigDecimal itemPriceBrutto;
  @JacksonXmlProperty(localName = "auction_id")
  private String auctionId;
  @JacksonXmlProperty(localName = "date_add")
  private String dateAdd;
}
