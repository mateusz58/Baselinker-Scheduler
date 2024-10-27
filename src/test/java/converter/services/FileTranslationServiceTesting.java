//package converter.services;
//
//import converter.FILES.CRT.FILES_TESTING_CRT;
//import converter.FILES.baselinker.FILES_BASELINKER_TESTING;
//import converter.configuration.FileFormatType;
//import converter.exceptions.ServiceOperationException;
//import converter.helper.FileHelper;
//import converter.mapper.Mapper;
//import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerXmlMapper;
//import converter.mapper.baselinker.orders.BaseLinkerXmlToPromoXmlMapper;
//import converter.mapper.baselinker.orders.BaseLinkerXmlToPromoXmlMapperNested;
//import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
//import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
//import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
//import converter.model.promoModel.OrderPromo;
//import converter.model.promoModel.OrdersPromo;
//import converter.serializers.ObjectsDeserializer;
//import converter.services.CRT.CrtFileTranslationToPromo;
//import org.apache.commons.io.FileUtils;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.xmlunit.diff.DefaultNodeMatcher;
//import org.xmlunit.diff.ElementSelectors;
//
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;
//
//public class FileTranslationServiceTesting {
//
//    CrtFileTranslationToPromo crtFileTranslationToPromo;
//
//    @BeforeAll
//    static void beforeAll() throws IOException {
//        FileHelper.createDirectory(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_DIRECTORY);
//    }
//
//    @BeforeEach
//    void beforeEach() throws IOException, ServiceOperationException {
//        //FileUtils.cleanDirectory(new File(FILES_TESTING_CRT.));
//        crtFileTranslationToPromo = new BaselinkerFileTranslationToPromo(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_DIRECTORY, new BaseLinkerXmlToPromoXmlMapper());
//    }
//
//    @Test
//    void mappingJsonXmlProccessWithSerializationAndDeserializationThenMappingFileToPromoShouldReturnTheSameFileAsExpectedWithDifferentShop() throws Exception {
//        File givenJsonFile = FILES_BASELINKER_TESTING.BASE_LINKER_JSON_DIFFERENT_SHOP;
//        File expected = (FILES_BASELINKER_TESTING.PROMO_DIFFERENT_SHOP);
//        OrdersBaseLinkerJson deserializedJsonApiObject;
//
//        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(givenJsonFile))) {
//            deserializedJsonApiObject = (OrdersBaseLinkerJson) ObjectsDeserializer.load(inputStream, OrdersBaseLinkerJson.class, FileFormatType.JSON);
//        }
//        OrdersBaseLinkerXml mappedXmlObject = mapperToBaseLinkerXml.map(deserializedJsonApiObject);
//
//        OrdersPromo ordersPromoToCheckTranslation = mapperToPromoXml.map(mappedXmlObject);
//        OrderPromo orderPromo;
//        baselinkerFileTranslationToPromo(Collections.singletonList(deserializedJsonApiObject));
//        List<String> outputDirectoryFiles = FileHelper.listAllFilePathsFromSelectedDirectory(FILES_BASELINKER_TESTING.BASE_LINKER_OUTPUT_DIRECTORY);
//
//        // check given
//        assertAll(() -> ObjectsDeserializer.load(new DataInputStream(new FileInputStream(givenJsonFile)), OrderBaseLinkerJson.class));
//
//        // check actual
//        for (String outputDirectoryFile : outputDirectoryFiles) {
//            DataInputStream inputStream = new DataInputStream(new FileInputStream(outputDirectoryFile));
//            orderPromo = (OrderPromo) ObjectsDeserializer.load(inputStream, OrderPromo.class, FileFormatType.XML);
//            inputStream.close();
//
//            assertThat(new File(outputDirectoryFile),
//                    isSimilarTo(expected)
//                            .ignoreWhitespace()
//                            .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
//                            .withAttributeFilter(s -> !s.getName().equals("orderSource"))
//                            .ignoreComments());
//        }
//    }
//}
