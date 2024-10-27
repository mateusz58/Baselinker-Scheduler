package converter.serializers;

import converter.configuration.FileFormatType;
import converter.configuration.ObjectMapperInstance;
import java.io.DataOutput;
import java.io.IOException;

public class ObjectSerializer {

  public static <T> void save(DataOutput dataOutput, T object, FileFormatType fileFormatType)
      throws IOException {
    if (fileFormatType == FileFormatType.XML) {
      ObjectMapperInstance.getInstanceXml().writeValue(dataOutput, object);
    } else {
      ObjectMapperInstance.getInstanceJson().writeValue(dataOutput, object);
    }
  }
}
