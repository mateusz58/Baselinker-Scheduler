package converter.model.baseLinkerModel.NoSql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Document(collection = "ordersBaselinker")
public class OrderNosql {

  @Id
  @JsonIgnore
  String mongoId;
  @Field("items")
  List<ItemNoSql> itemNoSqlList;
  @CreatedDate
  @Field("date_added")
  private LocalDateTime dateAddedToDatabase;
  @LastModifiedDate
  @Field("updated_date")
  private LocalDateTime lastUpdatedDateTimeInDatabase;
  @Field("order_id")
  private String orderId;
  @Field("order_date_add")
  private LocalDateTime orderDateAdd;
  @Field(value = "order_source")
  private String orderSource;
  @Field(value = "timestamp_GMT")
  private Long timestampGmt;
  @Field("payment_status")
  private Boolean paymentStatus;
  @Field("payment_paid")
  private BigDecimal paymentPaid;
  @Field("delivery_type")
  private String deliveryType;
  @Field("delivery_price")
  private BigDecimal deliveryPrice;
  @Field("client_login")
  private String clientLogin;
  @Field("client_name")
  private String clientName;
  @Field("client_company")
  private String clientCompany;
  @Field("client_nip")
  private String clientNip;
  @Field("client_postal")
  private String clientPostal;
  @Field("client_city")
  private String clientCity;
  @Field("client_address")
  private String clientAddress;
  @Field("address_name")
  private String addressName;
  @Field("address_company")
  private String addressCompany;
  @Field("address_country_code")
  private String addressCountryCode;
  @Field("address_postal")
  private String addressPostal;
  @Field("address_city")
  private String addressCity;
  @Field("address")
  private String address;
  @Field("order_amount_brutto")
  private String orderAmountBrutto;
  @Field("order_page")
  private String orderPage;
  @Field("ftp")
  private boolean ftp = false;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OrderNosql)) {
      return false;
    }
    OrderNosql that = (OrderNosql) o;
    return isFtp() == that.isFtp() &&
        getMongoId().equals(that.getMongoId()) &&
        Objects.equals(getItemNoSqlList(), that.getItemNoSqlList()) &&
        getOrderId().equals(that.getOrderId()) &&
        getOrderDateAdd().equals(that.getOrderDateAdd()) &&
        Objects.equals(getOrderSource(), that.getOrderSource()) &&
        getTimestampGmt().equals(that.getTimestampGmt()) &&
        Objects.equals(getPaymentStatus(), that.getPaymentStatus()) &&
        Objects.equals(getPaymentPaid(), that.getPaymentPaid()) &&
        getDeliveryType().equals(that.getDeliveryType()) &&
        getDeliveryPrice().equals(that.getDeliveryPrice()) &&
        getClientLogin().equals(that.getClientLogin()) &&
        getClientName().equals(that.getClientName()) &&
        Objects.equals(getClientCompany(), that.getClientCompany()) &&
        Objects.equals(getClientNip(), that.getClientNip()) &&
        getClientPostal().equals(that.getClientPostal()) &&
        getClientCity().equals(that.getClientCity()) &&
        getClientAddress().equals(that.getClientAddress()) &&
        getAddressName().equals(that.getAddressName()) &&
        Objects.equals(getAddressCompany(), that.getAddressCompany()) &&
        getAddressCountryCode().equals(that.getAddressCountryCode()) &&
        getAddressPostal().equals(that.getAddressPostal()) &&
        getAddressCity().equals(that.getAddressCity()) &&
        getAddress().equals(that.getAddress()) &&
        getOrderAmountBrutto().equals(that.getOrderAmountBrutto()) &&
        getOrderPage().equals(that.getOrderPage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMongoId(), getItemNoSqlList(), getOrderId(), getOrderDateAdd(),
        getOrderSource(), getTimestampGmt(), getPaymentStatus(), getPaymentPaid(),
        getDeliveryType(), getDeliveryPrice(), getClientLogin(), getClientName(),
        getClientCompany(), getClientNip(), getClientPostal(), getClientCity(), getClientAddress(),
        getAddressName(), getAddressCompany(), getAddressCountryCode(), getAddressPostal(),
        getAddressCity(), getAddress(), getOrderAmountBrutto(), getOrderPage(), isFtp());
  }
}
