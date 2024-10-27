package converter.model.baseLinkerModel.XML;

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
public class RowsXml {

  @JacksonXmlProperty(localName = "row")
  @JacksonXmlElementWrapper(localName = "row", useWrapping = false)
  List<RowXml> row;
}
