package converter.serializationDeserialization.BASELINKER.mapping;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.mapper.ObjectTranslator;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerNosql;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.NoSql.OrderNosql;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BaseLinkerNoSqlJsonMapping {

  OrdersBaseLinkerJson ordersBaseLinkerJson;
  OrderNosql ordersBaseLinkerNoSql;

  List<OrderNosql> ordersBaseLinkerNoSqlList = new ArrayList<OrderNosql>();

  private void deserializeFromFile(File file, Class type) throws IOException {
    if (type.equals(OrdersBaseLinkerJson.class)) {
      ordersBaseLinkerJson = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(file)), OrdersBaseLinkerJson.class,
          FileFormatType.JSON);
    }
    if (type.equals(OrderNosql.class)) {
      ordersBaseLinkerNoSql = (OrderNosql) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(file)), OrdersBaseLinkerJson.class,
          FileFormatType.JSON);
    }
  }

  private void serializeToFile(File file, Object object) throws IOException {
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(file)), object,
        FileFormatType.JSON);
  }

  @Test
  void testMapping() throws IOException, ParseException {
    //given
    deserializeFromFile(FILES_BASELINKER_TESTING.BASE_LINKER_JSON, OrdersBaseLinkerJson.class);

    ordersBaseLinkerNoSqlList = ObjectTranslator.translate(new BaseLinkerJsonToBaseLinkerNosql(),
        ordersBaseLinkerJson.getOrders());

    for (int i = 0; i < ordersBaseLinkerNoSqlList.size(); i++) {
      serializeToFile(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON,
          ordersBaseLinkerNoSqlList.get(i));
    }
  }
}
