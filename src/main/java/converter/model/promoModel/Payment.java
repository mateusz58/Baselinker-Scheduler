package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonPropertyOrder({
    "costs",
    "directdebit",
    "type",
})
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

  @JacksonXmlProperty(localName = "costs")
  private BigDecimal costs;
  @JacksonXmlProperty(localName = "directdebit")
  private String directdebit;
  @JacksonXmlProperty(localName = "type")
  private String type;
}
