package converter.model.baseLinkerModel.JSON;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    "order_amount_brutto",
    "rows"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderBaseLinkerJson {

  @JsonAlias("order_id")
  @JsonProperty("order_id")
  private String orderId;
  //    @JsonProperty(value = "date_confirmed")
//    @Getter(onMethod_ = {@JsonGetter(value = "date_add")}) -- only if you want to assign from date_confirmed_from
  @JsonProperty("date_add")
  private Long dateAddGmtUnixTimestamp;
  @JsonProperty("admin_comments")
  private String adminComments;
  @JsonProperty(value = "order_source")
  private String orderSource;
  @JsonAlias({"payment_method"})
  @JsonProperty("payment_name")
  private String paymentName;
  @JsonAlias({"confirmed"})
  @JsonProperty("payment_status")
  private Boolean paymentStatus;
  @JsonAlias({"payment_done"})
  @JsonProperty("payment_paid")
  private BigDecimal paymentPaid;
  @JsonAlias({"delivery_method"})
  @JsonProperty("delivery_type")
  private String deliveryType;
  @JsonAlias({"delivery_price"})
  @JsonProperty("delivery_price")
  private BigDecimal deliveryPrice;
  @JsonAlias({"user_comments"})
  @JsonProperty(value = "buyer_comments")
  private String buyerComments;
  @JsonAlias({"user_login"})
  @JsonProperty("client_login")
  private String clientLogin;
  @JsonAlias({"invoice_city"})
  @JsonProperty("client_city")
  private String clientCity;
  @JsonAlias({"delivery_postcode"})
  @JsonProperty("address_postal")
  private String addressPostal;
  @JsonAlias({"delivery_country_code"})
  @JsonProperty("address_country_code")
  private String addressCountryCode;
  @JsonAlias({"delivery_point_id"})
  @JsonProperty("address_box_machine")
  private String addressBoxMachine;
  @JsonAlias({"invoice_postcode"})
  @JsonProperty("client_postal")
  private String clientPostal;
  @JsonAlias({"delivery_fullname"})
  @JsonProperty("address_name")
  private String addressName;
  //@JsonAlias({"delivery_point_id"})
  @JsonProperty("address_pni")
  private String addressPni;
  @JsonAlias({"delivery_company"})
  @JsonProperty("address_company")
  private String addressCompany;
  @JsonAlias({"invoice_nip"})
  @JsonProperty("client_nip")
  private String clientNip;
  @JsonAlias({"invoice_address"})
  @JsonProperty("client_address")
  private String clientAddress;
  @JsonProperty("order_amount_brutto")
  private String orderAmountBrutto;
  @JsonProperty("client_name")
  @JsonAlias("invoice_fullname")
  private String clientName;
  @JsonAlias({"client_phone", "phone"})
  private String clientPhone;
  @JsonAlias({"invoice_company"})
  @JsonProperty("client_company")
  private String clientCompany;
  @JsonAlias({"delivery_address"})
  @JsonProperty("address")
  private String address;
  @JsonAlias({"delivery_country"})
  @JsonProperty("address_country")
  private String addressCountry;
  @JsonAlias({"email"})
  @JsonProperty("client_email")
  private String clientEmail;
  @JsonAlias({"delivery_city"})
  @JsonProperty("address_city")
  private String addressCity;
  @JsonProperty("order_page")
  private String orderPage;
  @JsonAlias({"want_invoice"})
  @JsonProperty("client_want_invoice")
  private String clientWantInvoice;
  @JsonAlias({"products"})
  @JsonProperty("rows")
  private List<RowJson> rows;
  @JsonUnwrapped
  @JsonIgnore
  private OrderIgnoredFieldsJson fields;
  @JsonProperty("delivery_package_module")
  private String deliveryPackageModule;
  @JsonProperty("delivery_package_nr")
  private String deliveryPackageNr;

  public void setAddressPni() {
    this.addressPni = this.addressBoxMachine;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OrderBaseLinkerJson)) {
      return false;
    }
    OrderBaseLinkerJson that = (OrderBaseLinkerJson) o;
    return getOrderId().equals(that.getOrderId()) &&
        getOrderSource().equals(that.getOrderSource()) &&
        getPaymentName().equals(that.getPaymentName()) &&
        getPaymentStatus().equals(that.getPaymentStatus()) &&
        getPaymentPaid().equals(that.getPaymentPaid()) &&
        getDeliveryType().equals(that.getDeliveryType()) &&
        getDeliveryPrice().equals(that.getDeliveryPrice()) &&
        getClientLogin().equals(that.getClientLogin()) &&
        getClientCity().equals(that.getClientCity()) &&
        getAddressPostal().equals(that.getAddressPostal()) &&
        getAddressCountryCode().equals(that.getAddressCountryCode()) &&
        Objects.equals(getAddressBoxMachine(), that.getAddressBoxMachine()) &&
        getClientPostal().equals(that.getClientPostal()) &&
        getAddressName().equals(that.getAddressName()) &&
        Objects.equals(getAddressPni(), that.getAddressPni()) &&
        Objects.equals(getAddressCompany(), that.getAddressCompany())
        && Objects.equals(getClientNip(), that.getClientNip())
        && getClientAddress().equals(that.getClientAddress())
        && Objects.equals(getOrderAmountBrutto(), that.getOrderAmountBrutto())
        && getClientName().equals(that.getClientName())
        && getClientPhone().equals(that.getClientPhone())
        && getClientCompany().equals(that.getClientCompany())
        && getAddress().equals(that.getAddress())
        && getAddressCountry().equals(that.getAddressCountry())
        && getClientEmail().equals(that.getClientEmail())
        && getAddressCity().equals(that.getAddressCity())
        && getOrderPage().equals(that.getOrderPage())
        && getClientWantInvoice().equals(that.getClientWantInvoice())
        && getRows().equals(that.getRows())
        && Objects.equals(getDeliveryPackageModule(), that.getDeliveryPackageModule())
        && Objects.equals(getDeliveryPackageNr(), that.getDeliveryPackageNr());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOrderId(), getOrderSource(), getPaymentName(), getPaymentStatus(),
        getPaymentPaid(), getDeliveryType(), getDeliveryPrice(), getClientLogin(), getClientCity(),
        getAddressPostal(), getAddressCountryCode(), getAddressBoxMachine(), getClientPostal(),
        getAddressName(), getAddressCompany(), getClientNip(), getClientAddress(),
        getOrderAmountBrutto(), getClientName(), getClientPhone(), getClientCompany(), getAddress(),
        getAddressCountry(), getClientEmail(), getAddressCity(), getOrderPage(),
        getClientWantInvoice(), getRows(), getDeliveryPackageModule(), getDeliveryPackageNr());
  }
}
