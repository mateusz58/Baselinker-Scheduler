package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonPropertyOrder({
    "code",
    "price",
    "desc",
})
@AllArgsConstructor
@NoArgsConstructor
public class Service {

  @JacksonXmlProperty(localName = "code")
  private String code;
  @JacksonXmlProperty(localName = "price")
  private Integer price;
  @JacksonXmlProperty(localName = "desc")
  private String desc;
}
