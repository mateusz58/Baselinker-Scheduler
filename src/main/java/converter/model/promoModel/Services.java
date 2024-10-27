package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonPropertyOrder({
    "service"
})
@AllArgsConstructor
@NoArgsConstructor
public class Services {

  @JacksonXmlElementWrapper(localName = "service")
  private Service service;
}
