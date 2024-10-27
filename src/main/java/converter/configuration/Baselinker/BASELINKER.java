package converter.configuration.Baselinker;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class BASELINKER {

  public static String TOKEN;

  public static String GATEWAY;

  public static String INVENTORY_FAINA_OFFPRICE;

  public static String INVENTORY_PANDA;

  public static String WAREHOUSE_ID;

  @Value("${baselinker.inventory.faina-offprice}")
  public static void setINVENTORY_FAINA_OFFPRICE(String inventoryFainaOffprice) {
    INVENTORY_FAINA_OFFPRICE = inventoryFainaOffprice;
  }

  @Value("${baselinker.inventory.faina-panda}")
  public static void setINVENTORY_PANDA(String inventoryPanda) {
    INVENTORY_PANDA = inventoryPanda;
  }

  @Value("${baselinker.warehouse.id}")
  public static void setWAREHOUSE_ID(String warehouseId) {
    WAREHOUSE_ID = warehouseId;
  }

  @Value("${baselinker.token}")
  public void setToken(final String token) {
    TOKEN = token;
  }

  @Value("${baselinker.gateway}")
  public void setGateway(final String gateway) {
    GATEWAY = gateway;
  }
}
