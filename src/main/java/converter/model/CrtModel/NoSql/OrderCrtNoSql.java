package converter.model.CrtModel.NoSql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import converter.model.CrtModel.JSON.Item;
import converter.model.CrtModel.JSON.StateOrderEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ordersCrt")
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCrtNoSql {

  @Id
  @JsonIgnore
  String mongoId;

  @Field("order_id")
  private String orderId;

  @Field("order_number")
  private String orderNumber;

  @CreatedDate
  @Field("date_added")
  private LocalDateTime dateAdded;
  @LastModifiedDate
  @Field("updated_date")
  private LocalDateTime updatedDate;

  @Field("state")
  private StateOrderEnum state;

  @Field("events")
  private List<EventCrtNoSql> eventCrtNoSqlList = new LinkedList<>();

  @Field("items")
  private List<Item> items = new ArrayList<>();

  @Field("ftp")
  private boolean isSentToFtp = false;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OrderCrtNoSql)) {
      return false;
    }

    OrderCrtNoSql that = (OrderCrtNoSql) o;
    if (!getOrderId().equals(that.getOrderId())) {
      return false;
    }
    if (!getOrderNumber().equals(that.getOrderNumber())) {
      return false;
    }
    if (!getEventCrtNoSqlList().equals(that.getEventCrtNoSqlList())) {
      return false;
    }
    if (!getItems().equals(that.getItems())) {
      return false;
    }
    return getState().equals(that.getState());
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + getOrderId().hashCode();
    result = 31 * result + getOrderNumber().hashCode();
    result = 31 * result + getEventCrtNoSqlList().hashCode();
    return result;
  }
}
