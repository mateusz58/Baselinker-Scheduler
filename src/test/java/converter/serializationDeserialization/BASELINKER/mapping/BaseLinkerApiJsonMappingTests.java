package converter.serializationDeserialization.BASELINKER.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
import converter.configuration.FileFormatType;
import converter.helper.FileHelper;
import converter.mapper.Mapper;
import converter.mapper.ObjectTranslator;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerXmlMapper;
import converter.mapper.baselinker.orders.BaseLinkerXmlToPromoXmlMapperNested;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.model.promoModel.OrdersPromo;
import converter.serializers.ObjectSerializer;
import converter.serializers.ObjectsDeserializer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;

public class BaseLinkerApiJsonMappingTests {

  Mapper<OrdersBaseLinkerJson, OrdersBaseLinkerXml> mapperToBaseLinkerXml = new BaseLinkerJsonToBaseLinkerXmlMapper();
  Mapper<OrdersBaseLinkerXml, OrdersPromo> mapperToPromoXml = new BaseLinkerXmlToPromoXmlMapperNested();

  OrdersBaseLinkerJson ordersBaseLinkerJson;
  OrdersBaseLinkerXml ordersBaseLinkerXml;
  OrdersPromo ordersPromo;

  private void deserializeFromFile(File file, Class type) throws IOException {
    if (type.equals(OrdersBaseLinkerJson.class)) {
      ordersBaseLinkerJson = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(file)), OrdersBaseLinkerJson.class,
          FileFormatType.JSON);
    }
    if (type.equals(OrdersBaseLinkerXml.class)) {
      ordersBaseLinkerXml = (OrdersBaseLinkerXml) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(file)), OrdersBaseLinkerXml.class,
          FileFormatType.XML);
    }
    if (type.equals(OrdersPromo.class)) {
      ordersPromo = (OrdersPromo) ObjectsDeserializer.load(
          new DataInputStream(new FileInputStream(file)), OrdersPromo.class, FileFormatType.XML);
    }
  }

  @BeforeEach
  void beforeEach() throws IOException {
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_JSON));
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_XML));
    FileHelper.clear(String.valueOf(FILES_BASELINKER_TESTING.PROMO_OUTPUT));
  }

  @Test
  void mappingJsonXmlProcessWithSerializationAndDeserializationShouldSaveTheSameFileAsExpected1()
      throws IOException, ParseException {
    File given = FILES_BASELINKER_TESTING.BASE_LINKER_JSON;
    File actual = new File(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_XML.getAbsolutePath());
    File expected = (FILES_BASELINKER_TESTING.BASE_LINKER_XML);

    deserializeFromFile(given, OrdersBaseLinkerJson.class);
    OrdersBaseLinkerXml mappedXmlObject = ObjectTranslator.translate(mapperToBaseLinkerXml,
        ordersBaseLinkerJson);
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)), mappedXmlObject,
        FileFormatType.XML);

    assertThat(actual,
        isSimilarTo(expected)
            .ignoreWhitespace()
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .withAttributeFilter(s -> !s.getName().equals("orderSource"))
            .ignoreComments()
    );
  }

  @Test
  void mappingProcessShouldCalculateOrderAmountBrutto() throws IOException, ParseException {
    //given
    File given = new File(FILES_BASELINKER_TESTING.BASE_LINKER_JSON_MULTIPLE.getAbsolutePath());
    File actual = new File(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_XML.getAbsolutePath());
    BigDecimal expected = BigDecimal.valueOf(6.00).setScale(2);
    //when
    deserializeFromFile(given, OrdersBaseLinkerJson.class);
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)), ordersBaseLinkerJson,
        FileFormatType.XML);
    OrdersBaseLinkerXml mapped = mapperToBaseLinkerXml.map(ordersBaseLinkerJson);
    //then
    assertEquals(expected, mapped.getOrders().get(0).getOrderAmountBrutto().setScale(2));
  }

  @Test
  void mappingJsonXmlProccessWithSerializationAndDeserializationThenMappingFileToPromoShouldReturnTheSameFileAsExpected()
      throws IOException, ParseException {
    File given = FILES_BASELINKER_TESTING.BASE_LINKER_JSON;
    File actual = FILES_BASELINKER_TESTING.PROMO_OUTPUT;
    File expected = FILES_BASELINKER_TESTING.PROMO;

    deserializeFromFile(given, OrdersBaseLinkerJson.class);
    OrdersBaseLinkerXml mappedXmlObject = mapperToBaseLinkerXml.map(ordersBaseLinkerJson);

    OrdersPromo ordersPromo = mapperToPromoXml.map(mappedXmlObject);
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)),
        ordersPromo.getOrders().get(0), FileFormatType.XML);

    assertThat(actual,
        isSimilarTo(expected)
            .ignoreWhitespace()
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .withAttributeFilter(s -> !s.getName().equals("orderSource"))
            .ignoreComments()
    );
  }

  @Test
  void mappingJsonXmlProccessWithSerializationAndDeserializationThenMappingFileToPromoShouldReturnTheSameFileAsExpectedWithGivenPromoFileSampleWithDifferentShops()
      throws IOException, ParseException {
    File given = FILES_BASELINKER_TESTING.BASE_LINKER_JSON_DIFFERENT_SHOP;
    File actual = new File(FILES_BASELINKER_TESTING.PROMO_OUTPUT.getAbsolutePath());
    File expected = (FILES_BASELINKER_TESTING.PROMO_DIFFERENT_SHOP);

    OrdersBaseLinkerJson deserializedJsonApiObject = (OrdersBaseLinkerJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(given)), OrdersBaseLinkerJson.class,
        FileFormatType.JSON);
    OrdersBaseLinkerXml mappedXmlObject = mapperToBaseLinkerXml.map(deserializedJsonApiObject);

    OrdersPromo ordersPromo = mapperToPromoXml.map(mappedXmlObject);
    ObjectSerializer.save(new DataOutputStream(new FileOutputStream(actual)),
        ordersPromo.getOrders().get(0), FileFormatType.XML);

    assertThat(actual,
        isSimilarTo(expected)
            .ignoreWhitespace()
            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
            .withAttributeFilter(s -> !s.getName().equals("orderSource"))
            .ignoreComments()
    );
  }
}
