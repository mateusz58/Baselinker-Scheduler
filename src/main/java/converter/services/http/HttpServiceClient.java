package converter.services.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

public class HttpServiceClient {

  public static HttpResponse<?> post(HttpRequest httpRequest)
      throws IOException, InterruptedException {
    return Optional.ofNullable(
            HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString()))
        .orElseThrow(() -> new ConnectException("No internet or server connection"));
  }
}
