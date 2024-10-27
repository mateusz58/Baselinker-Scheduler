package converter.controller.crt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import converter.configuration.ObjectMapperInstance;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ControllerHelper {

  public static <T> ResponseEntity<T> createJsonOkResponse(T body) {
    if (body == null) {
      throw new IllegalArgumentException("Response body cannot be null");
    }
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(body, responseHeaders, HttpStatus.OK);
  }

  public static Object parseJson(String json, Class type) throws IOException {
    JsonParser parser = ObjectMapperInstance.getInstanceJson().getFactory().createParser(json);
    JsonNode node = parser.getCodec().readTree(parser);
    return ObjectMapperInstance.getInstanceJson().treeToValue(node, type);
  }

  public static boolean isAuthorized(String token, String tokenValue) {
    return token != null && token.equals(tokenValue);
  }
}
