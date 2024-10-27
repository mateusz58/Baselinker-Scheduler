package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JacksonXmlRootElement(localName = "ship_to")
//@JsonIgnoreProperties(value = {"channel_no"})
@JsonPropertyOrder({
    "tb_id",
    "channel_no",
    "firstname",
    "lastname",
    "name",
    "street_no",
    "street_extension",
    "zip",
    "city",
    "country",
    "email",
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipTo {

  @JacksonXmlProperty(localName = "zip")
  private String zip;
  @JacksonXmlProperty(localName = "country")
  private String country;
  @JacksonXmlProperty(localName = "title")
  private String title;
  @JacksonXmlProperty(localName = "firstname")
  private String firstname;
  @JacksonXmlProperty(localName = "city")
  private String city;
  @JacksonXmlProperty(localName = "street_no")
  private String streetNo;
  @JacksonXmlProperty(localName = "channel_no")
  private String channelNo;
  @JacksonXmlProperty(localName = "name")
  private String name;
  @JacksonXmlProperty(localName = "tb_id")
  private String tbId;
  //  @JacksonXmlProperty(localName = "street_extension", isAttribute = false)
//  private String streetExtension;
  @JacksonXmlProperty(localName = "email")
  private String email;
  @JacksonXmlProperty(localName = "lastname")
  private String lastname;
}
