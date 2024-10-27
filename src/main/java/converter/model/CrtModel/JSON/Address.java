package converter.model.CrtModel.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Address {

  @JsonProperty("first_name")
  String firstName;
  @JsonProperty("last_name")
  String lastName;
  @JsonProperty("address_line_1")
  String addressLine1;
  @JsonProperty("zip_code")
  String zipCode;
  @JsonProperty("city")
  String city;
  @JsonProperty("country_code")
  String countryCode;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Address)) {
      return false;
    }
    Address address = (Address) o;
    return getFirstName().equals(address.getFirstName()) && getLastName().equals(
        address.getLastName()) && getAddressLine1().equals(address.getAddressLine1())
        && getZipCode().equals(address.getZipCode()) && getCity().equals(address.getCity())
        && getCountryCode().equals(address.getCountryCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFirstName(), getLastName(), getAddressLine1(), getZipCode(), getCity(),
        getCountryCode());
  }
}
