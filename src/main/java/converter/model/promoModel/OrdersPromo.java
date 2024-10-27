package converter.model.promoModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersPromo {

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "order")
  List<OrderPromo> orders;
}
