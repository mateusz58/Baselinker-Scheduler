package converter.model.baseLinkerModel.XML;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@JsonPropertyOrder({
    "order_id",
    "date_add",
    "payment_name",
    "payment_status",
    "payment_paid",
    "delivery_type",
    "delivery_price",
    "buyer_comments",
    "seller_comments",
    "client_login",
    "client_email",
    "client_phone",
    "client_name",
    "client_company",
    "client_nip",
    "client_postal",
    "client_city",
    "client_address",
    "client_want_invoice",
    "address_name",
    "address_company",
    "address_country",
    "address_country_code",
    "address_box_machine",
    "address_pni",
    "address_postal",
    "address_city",
    "address",
    "order_amount_brutto"
    , "rows"
})
@Data
public class OrderBaseLinkerXml {

  @JacksonXmlProperty(localName = "order_id")
  private String orderId;
  @JacksonXmlProperty(isAttribute = true)
  private String orderSource;
  @JacksonXmlProperty(localName = "date_add")
  private String dateAdd;
  @JacksonXmlProperty(localName = "payment_name")
  private String paymentName;
  @JacksonXmlProperty(localName = "payment_status")
  private Integer paymentStatus;
  @JacksonXmlProperty(localName = "payment_paid")
  private BigDecimal paymentPaid;
  @JacksonXmlProperty(localName = "delivery_type")
  private String deliveryType;
  @JacksonXmlProperty(localName = "delivery_price")
  private BigDecimal deliveryPrice;
  @JacksonXmlProperty(localName = "buyer_comments")
  private String buyerComments;
  @JacksonXmlProperty(localName = "seller_comments")
  private String sellerComments;
  @JacksonXmlProperty(localName = "client_login")
  private String clientLogin;
  @JacksonXmlProperty(localName = "client_city")
  private String clientCity;
  @JacksonXmlProperty(localName = "address_postal")
  private String addressPostal;
  @JacksonXmlProperty(localName = "address_country_code")
  private String addressCountryCode;
  @JacksonXmlProperty(localName = "address_box_machine")
  private String addressBoxMachine;
  @JacksonXmlProperty(localName = "client_postal")
  private String clientPostal;
  @JacksonXmlProperty(localName = "address_name")
  private String addressName;
  @JacksonXmlProperty(localName = "address_pni")
  private String addressPni;
  @JacksonXmlProperty(localName = "address_company")
  private String addressCompany;
  @JacksonXmlProperty(localName = "client_nip")
  private String clientNip;
  @JacksonXmlProperty(localName = "client_address")
  private String clientAddress;
  @JacksonXmlProperty(localName = "order_amount_brutto")
  private BigDecimal orderAmountBrutto;
  @JacksonXmlProperty(localName = "client_name")
  private String clientName;
  @JacksonXmlProperty(localName = "client_phone")
  private String clientPhone;
  @JacksonXmlProperty(localName = "client_company")
  private String clientCompany;
  @JacksonXmlProperty(localName = "address")
  private String address;
  @JacksonXmlProperty(localName = "address_country")
  private String addressCountry;
  @JacksonXmlProperty(localName = "client_email")
  private String clientEmail;
  @JacksonXmlProperty(localName = "address_city")
  private String addressCity;
  @JacksonXmlProperty(localName = "client_want_invoice")
  private Integer clientWantInvoice;
  @JacksonXmlElementWrapper(localName = "rows")
  private RowsXml rows;

  public OrderBaseLinkerXml() {
  }
}
