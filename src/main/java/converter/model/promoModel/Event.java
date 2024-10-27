package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonPropertyOrder({
    "event_id",
    "event_type",
    "date_created",
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

  @JacksonXmlProperty(localName = "event_id")
  private String eventId;
  @JacksonXmlProperty(localName = "event_type")
  private String eventType;
  @JacksonXmlProperty(localName = "date_created")
  private String dateCreated;
}
