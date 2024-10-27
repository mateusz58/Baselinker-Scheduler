package converter.controller.crt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import converter.FILES.CRT.FILES_TESTING_CRT;
import converter.configuration.ObjectMapperInstance;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.serializers.ObjectsDeserializer;
import converter.services.DatabaseServiceOrdersInterface;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@WebMvcTest(value = CRTController.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
class CRTControllerTestDatabase {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");
  @Qualifier("CRT-DATABASE-SERVICE")
  @Autowired
  DatabaseServiceOrdersInterface crtService;
  @Value("${endPointCrt}")
  private String url;
  @Value("${x-api-key}")
  private String token;
  @Autowired
  private MockMvc mockMvc;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Test
  void postCrtOrderShouldAuthorizeAndAddOrderAndReturnOk() throws Exception {
    EventCrtJson givenObject = (EventCrtJson) ObjectsDeserializer.load(
        new DataInputStream(new FileInputStream(FILES_TESTING_CRT.CRT_JSON_EVENT)),
        EventCrtJson.class);
    String jsonGiven = ObjectMapperInstance.getInstanceJson().writeValueAsString(givenObject);
    HttpHeaders headers = new HttpHeaders();
    headers.put("x-api-key", Collections.singletonList(token));

    mockMvc.perform(post(url)
            .headers(headers)
            .content(jsonGiven)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(jsonGiven));
  }
}
