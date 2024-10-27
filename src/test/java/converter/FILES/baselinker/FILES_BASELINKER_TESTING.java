package converter.FILES.baselinker;

import java.io.File;

public class FILES_BASELINKER_TESTING {

  public static final String ORDERS_PROCESSED_DIRECTORY = new File(
      "src/test/resources/baselinker/orders_processed/").getPath();

  public static String BASE_LINKER_OUTPUT_DIRECTORY = "src/test/resources/baselinker/output/";
  public static File BASE_LINKER_JSON = new File(
      "src/test/resources/baselinker/baselinker-api.json");
  public static File BASE_LINKER_JSON_DIFFERENT_SHOP = new File(
      "src/test/resources/baselinker/baselinker-api_differentShop.json");
  public static File BASE_LINKER_JSON_MULTIPLE = new File(
      "src/test/resources/baselinker/baselinker_multiple_Items.json");
  public static File BASE_LINKER_JSON_MANY_ORDERS = new File(
      "src/test/resources/baselinker/baselinker_many_orders.json");
  public static File BASE_LINKER_XML = new File("src/test/resources/baselinker/baselinker.xml");

  public static File PROMO = new File("src/test/resources/baselinker/promo_allegro_sample.xml");
  public static File PROMO_DIFFERENT_SHOP = new File(
      "src/test/resources/baselinker/promo-differentShopSample.xml");
  public static File PROMO_OUTPUT = new File("src/test/resources/baselinker/promo_output.xml");

  public static File BASE_LINKER_OUTPUT_JSON = new File(
      "src/test/resources/baselinker/output.json");

  public static File BASE_LINKER_PRODUCT_INVENTORY_JSON = new File(
      "src/test/resources/baselinker/stocks/products.json");
  public static File BASE_LINKER_OUTPUT_XML = new File("src/test/resources/baselinker/output.xml");
}
