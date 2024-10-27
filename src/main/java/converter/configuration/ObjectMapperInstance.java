package converter.configuration;

import com.ctc.wstx.api.WstxOutputProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperInstance {

  private static XmlMapper objectMapperXml;
  private static ObjectMapper objectMapperJson;

  private ObjectMapperInstance() {
  }

  public static XmlMapper getInstanceXml() {
    if (objectMapperXml == null) {
      objectMapperXml = new XmlMapper();
      objectMapperXml.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
      objectMapperXml.getFactory().getXMLOutputFactory()
          .setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, true);
      objectMapperXml.enable(SerializationFeature.INDENT_OUTPUT);
    }
    return objectMapperXml;
  }

  public static ObjectMapper getInstanceJson() {
    if (objectMapperJson == null) {
      objectMapperJson = new ObjectMapper();
      objectMapperJson.enable(SerializationFeature.INDENT_OUTPUT);
      objectMapperJson.registerModule(new JavaTimeModule());
      objectMapperJson.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      objectMapperJson.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    return objectMapperJson;
  }
}
