package converter.model.baseLinkerModel.XML;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersBaseLinkerXml {

  @JacksonXmlElementWrapper(localName = "order", useWrapping = false)
  @JacksonXmlProperty(localName = "order")
  private List<OrderBaseLinkerXml> orders;
}
