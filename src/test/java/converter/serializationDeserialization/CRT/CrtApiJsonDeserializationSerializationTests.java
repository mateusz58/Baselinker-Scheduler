package converter.serializationDeserialization.CRT;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import converter.FILES.CRT.FILES_TESTING_CRT;
import converter.configuration.FileFormatType;
import converter.helper.FileHelper;
import converter.mapper.ObjectTranslator;
import converter.mapper.crt.CrtEventJsonToOrderCrtJsonMapper;
import converter.mapper.crt.CrtJsonToPromo;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.promoModel.OrderPromo;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CrtApiJsonDeserializationSerializationTests {

  List<EventCrtJson> events = new LinkedList<>();

  @BeforeEach
  void beforeEach() throws IOException {
    FileHelper.clear(String.valueOf(FILES_TESTING_CRT.CRT_OUTPUT_JSON));
  }

  void loadAllEventsFromFolder() throws IOException {
    List<String> loadedFilesPathsFromFolder = FileHelper.listAllFilePathsFromSelectedDirectory(
        FILES_TESTING_CRT.CRT_EVENTS_DIRECTORY_SAMPLES.toString());
    List<EventCrtJson> events = new LinkedList<>();
    for (String filePath : loadedFilesPathsFromFolder) {
      DataInputStream inputStream = new DataInputStream(new FileInputStream(filePath));
      EventCrtJson givenObject = (EventCrtJson) ObjectsDeserializer.load(inputStream,
          EventCrtJson.class);
      inputStream.close();
      events.add(givenObject);
    }
    this.events = events;
  }

  @Test
  void deserializationOfJsonShouldNotThrowAnyException() throws IOException {
    loadAllEventsFromFolder();
  }

  @Test
  void checkLoadedEventsFromDirectory() throws IOException {
    loadAllEventsFromFolder();

    assertTrue(events.get(0).getClass().isAssignableFrom(EventCrtJson.class));
  }

  @Test
  void shouldSerializeToFileWWithoutThrowingException() throws IOException {
    loadAllEventsFromFolder();

    assertAll(() -> ObjectSerializer.save(
        new DataOutputStream(new FileOutputStream(FILES_TESTING_CRT.CRT_OUTPUT_JSON)),
        events.get(0), FileFormatType.JSON));
  }

  @Test
  void shouldTranslateToPromoXmlAndSerialize() throws IOException, ParseException {
    loadAllEventsFromFolder();

    OrderCrtJson translatedOrderFromEvent = ObjectTranslator.translate(
        new CrtEventJsonToOrderCrtJsonMapper(), events.get(2));

    assertTrue(translatedOrderFromEvent.getClass().isAssignableFrom(OrderCrtJson.class));

    OrderPromo promoTranslated = ObjectTranslator.translate(new CrtJsonToPromo(),
        translatedOrderFromEvent);

    ObjectSerializer.save(
        new DataOutputStream(new FileOutputStream(FILES_TESTING_CRT.CRT_OUTPUT_XML)),
        promoTranslated, FileFormatType.XML);
  }
}
