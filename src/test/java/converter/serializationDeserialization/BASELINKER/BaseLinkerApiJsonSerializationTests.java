package converter.serializationDeserialization.BASELINKER;

import static org.junit.jupiter.api.Assertions.assertAll;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.helper.FileHelper;
import converter.mapper.Mapper;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerXmlMapper;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BaseLinkerApiJsonSerializationTests {

  Mapper<OrdersBaseLinkerJson, OrdersBaseLinkerXml> mapper = new BaseLinkerJsonToBaseLinkerXmlMapper();

  @BeforeEach
  void beforeEach() throws IOException {
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON));
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_XML));
  }

  @Test
  void serializationProcessShouldNotThrowAnyExceptionDuringStandardRun() throws IOException {
    //given
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON.getAbsolutePath());
    File actual = new File(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON.getAbsolutePath());
    //when
    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)),
        deserializedJsonApiObject, FileFormatType.XML);
    //then
    assertAll(() -> ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)),
        deserializedJsonApiObject, FileFormatType.XML));
  }
}
