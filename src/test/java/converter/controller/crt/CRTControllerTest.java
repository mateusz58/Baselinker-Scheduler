package converter.controller.crt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import converter.configuration.MongoConfiguration;
import converter.configuration.ObjectMapperInstance;
import converter.configuration.WebSecurity.CRT.WebSecurityConfigTestAuthorization;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.services.DatabaseServiceOrdersImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = CRTController.class)
@ExtendWith(SpringExtension.class)
@Import({MongoConfiguration.class, WebSecurityConfigTestAuthorization.class})
class CRTControllerTest {

  @MockBean
  DatabaseServiceOrdersImpl crtService;

  @Value("${endPointCrt}")
  private String url;

  @Value("${x-api-key}")
  private String token;
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnAllOrders() throws Exception {
    List<OrderCrtJson> objects = List.of(OrderCrtJson.builder().orderNumber("order_1").build(),
        OrderCrtJson.builder().orderNumber("order_2").build());
    when(crtService.getAll()).thenReturn(objects);
    HttpHeaders headers = new HttpHeaders();
    headers.put("x-api-key", Collections.singletonList(token));

    mockMvc.perform(get(url)
            .headers(headers)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content().json(ObjectMapperInstance.getInstanceJson().writeValueAsString(objects)));

    verify(crtService).getAll();
  }

  @Test
  public void shouldReturnEmptyListWhenThereAreNoOrders() throws Exception {
    List<OrderCrtJson> objects = new ArrayList<>();
    when(crtService.getAll()).thenReturn(objects);
    HttpHeaders headers = new HttpHeaders();
    headers.put("x-api-key", Collections.singletonList(token));

    mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content().json(ObjectMapperInstance.getInstanceJson().writeValueAsString(objects)));

    verify(crtService).getAll();
  }

  @Test
  void postCrtOrderShouldAuthorizeAndAddOrderAndReturnOk() throws Exception {
    EventCrtJson given = EventCrtJson.builder().orderId("order_1").orderNumber("name_1").build();
    ArgumentCaptor<OrderCrtJson> argument = ArgumentCaptor.forClass(OrderCrtJson.class);
    HttpHeaders headers = new HttpHeaders();
    headers.put("x-api-key", Collections.singletonList(token));

    mockMvc.perform(post(url)
            .headers(headers)
            .content("{\"order_id\": \"order_1\", \"order_number\": \"name_1\"}")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content().json(ObjectMapperInstance.getInstanceJson().writeValueAsString(given)));

    verify(crtService).add(argument.capture());
    assertEquals(given.getOrderId(), argument.getValue().getOrderId());
    assertEquals(given.getOrderNumber(), argument.getValue().getOrderNumber());
  }
}
