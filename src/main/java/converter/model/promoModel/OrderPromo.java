package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "order_data",
    "sell_to",
    "ship_to",
    "shipment",
    "payment",
    "history",
    "services",
    "items"
})
@Builder(toBuilder = true)
@JacksonXmlRootElement(localName = "order")
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPromo {

  @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsd")
  private final String xmlns = "http://www.w3.org/2001/XMLSchema";

  @JacksonXmlProperty(localName = "order_data")
  private OrderData orderData;
  @JacksonXmlProperty(localName = "sell_to")
  private SellTo sellTo;
  @JacksonXmlProperty(localName = "ship_to")
  private ShipTo shipTo;
  @JacksonXmlProperty(localName = "shipment")
  private Shipment shipment;
  @JacksonXmlProperty(localName = "payment")
  private Payment payment;
  @JacksonXmlProperty(localName = "history")
  private History history;
  @JacksonXmlProperty(localName = "services")
  @JsonIgnore
  private Services services;
  @JacksonXmlProperty(localName = "items")
  private ItemsPromo itemsPromo;

  public OrderPromo() {
  }
}
