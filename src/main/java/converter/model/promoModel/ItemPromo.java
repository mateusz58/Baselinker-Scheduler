package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonPropertyOrder({
    "tb_id",
    "channel_id",
    "sku",
    "channel_sku",
    "ean",
    "quantity",
    "billing_text",
    "transfer_price",
    "item_price",
    "date_created"
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemPromo {

  @JacksonXmlProperty(localName = "tb_id")
  private String tbId;
  @JacksonXmlProperty(localName = "channel_id")
  private String channelId;
  @JacksonXmlProperty(localName = "sku")
  private String sku;
  @JacksonXmlProperty(localName = "channel_sku")
  private String channelSku;
  @JacksonXmlProperty(localName = "ean")
  private String ean;
  @JacksonXmlProperty(localName = "quantity")
  private Integer quantity;
  @JacksonXmlProperty(localName = "billing_text")
  private String billingText;
  @JacksonXmlProperty(localName = "transfer_price")
  @JsonIgnore
  private BigDecimal transferPrice;
  @JacksonXmlProperty(localName = "item_price")
  private BigDecimal itemPrice;
  @JacksonXmlProperty(localName = "date_created")
  private String dateCreated;
}
