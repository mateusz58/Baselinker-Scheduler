package converter.serializationDeserialization.BASELINKER;

import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.helper.FileHelper;
import converter.model.baseLinkerModel.JSON.stocks.ProductsJson;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BaseLinkerApiJsonProductsDeserializationTests {

  ObjectsDeserializer deserialize;

  @BeforeEach
  void beforeEach() throws IOException {
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON));
  }

  @Test
  void deserializationOfJsonShouldNotThrowAnyException() throws IOException {
    File given = FILES_BASELINKER_TESTING.BASE_LINKER_PRODUCT_INVENTORY_JSON;
    //when
    ProductsJson deserializedJsonApiObject = (ProductsJson) deserialize.load(
        new DataInputStream(new FileInputStream(given)), ProductsJson.class, FileFormatType.JSON);
    //then
    assertTrue(deserializedJsonApiObject.getClass().isAssignableFrom(ProductsJson.class));
  }
}
