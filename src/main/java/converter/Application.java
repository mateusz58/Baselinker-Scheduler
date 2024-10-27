package converter;

import ch.qos.logback.classic.Logger;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class Application {

  private static final Logger log = (Logger) LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) throws IOException {
    SpringApplication.run(Application.class, args);
  }
}
