package converter.model.promoModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
public class SellTo {

  @JacksonXmlProperty(localName = "tb_id")
  private String tbISellTo;
  @JacksonXmlProperty(localName = "title")
  private String title;
  @JacksonXmlProperty(localName = "channel_no")
  private String channelNo;
  @JacksonXmlProperty(localName = "firstname")
  private String firstname;
  @JacksonXmlProperty(localName = "lastname")
  private String lastname;
  @JacksonXmlProperty(localName = "name")
  private String name;
  @JacksonXmlProperty(localName = "street_no")
  private String streetNo;
  //  @JacksonXmlProperty(localName = "street_extension")
//  private String streetExtension;
  @JacksonXmlProperty(localName = "zip")
  private String zip;
  @JacksonXmlProperty(localName = "city")
  private String city;
  @JacksonXmlProperty(localName = "country")
  private String country;
  @JacksonXmlProperty(localName = "email")
  private String email;
}