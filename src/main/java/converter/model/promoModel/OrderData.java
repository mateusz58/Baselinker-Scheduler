package converter.model.promoModel;

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
    "order_date",
    "tb_id",
    "channel_sign",
    "channel_id",
    "channel_no",
    "fix_dhl_id",
    "fix_dhlRetour_id",
    "paid",
    "approved",
    "item_count",
    "total_item_amount",
    "date_created"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderData {

  @JacksonXmlProperty(localName = "fix_dhl_id")
  String fixDhlIdCrtOnly;
  @JacksonXmlProperty(localName = "fix_dhlRetour_id")
  String fixDhlRetourIdCrtOnly;
  @JacksonXmlProperty(localName = "channel_sign")
  private String channelSign;
  @JacksonXmlProperty(localName = "item_count")
  private Integer itemCount;
  @JacksonXmlProperty(localName = "order_date")
  private String orderDate;
  @JacksonXmlProperty(localName = "approved")
  private Integer approved;
  @JacksonXmlProperty(localName = "total_item_amount")
  private BigDecimal totalItemAmount;
  @JacksonXmlProperty(localName = "date_created")
  private String dateCreated;
  @JacksonXmlProperty(localName = "channel_no")
  private String channelNo;
  @JacksonXmlProperty(localName = "paid")
  private BigDecimal paid;
  @JacksonXmlProperty(localName = "tb_id")
  private String tbIdOrderData;
  @JacksonXmlProperty(localName = "channel_id")
  private String channelId;
}
