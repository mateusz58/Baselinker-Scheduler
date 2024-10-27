package converter.model.promoModel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemsPromo {

  @JacksonXmlProperty(localName = "item")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<ItemPromo> itemPromo;
}
