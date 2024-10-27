package converter.serializers;

import converter.configuration.FileFormatType;
import converter.configuration.ObjectMapperInstance;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectsDeserializer {

  private static final Logger log
      = LoggerFactory.getLogger(ObjectsDeserializer.class);

  public static Object load(DataInput input, Class targetClass, FileFormatType fileFormatType)
      throws IOException {
    if (fileFormatType == FileFormatType.XML) {
      InputStream inputStream = (InputStream) input;
      return ObjectMapperInstance.getInstanceXml()
          .readValue(inputStream, targetClass);
    }
    return ObjectMapperInstance.getInstanceJson()
        .readValue(input, targetClass);
  }

  public static Object load(DataInput input, Class targetClass) throws IOException {
    return ObjectMapperInstance.getInstanceJson()
        .readValue(input, targetClass);
  }
}
