package converter.model.baseLinkerModel.JSON;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("orders")
public class OrdersBaseLinkerJson {

  @JsonAlias({"orders"})
  private List<OrderBaseLinkerJson> orders;
  private String status;
}
