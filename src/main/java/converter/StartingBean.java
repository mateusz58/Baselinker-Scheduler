package converter;

import ch.qos.logback.classic.Logger;
import converter.configuration.FILES;
import converter.configuration.FTP_SERVER;
import converter.helper.FileHelper;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartingBean implements org.springframework.beans.factory.InitializingBean {

  private static final Logger log = (Logger) LoggerFactory.getLogger(Application.class);
  @Autowired
  MongoTemplate mongoTemplate;
  @Autowired
  FILES files;
  @Autowired
  FTP_SERVER ftp_server;
  @Autowired
  private Environment environment;

  private void checkDatabaseConnection() {
    String mongoUri = environment.getProperty("spring.data.mongodb.uri");
    log.debug("Attempting to connect to Mongo URI: " + mongoUri);
    try {
      mongoTemplate.getCollectionNames();
      log.debug("MongoDB connection is successful and authenticated");
    } catch (Exception e) {
      log.debug("MongoDB connection failed");
      log.debug(e.getMessage());
    }
  }

  private void createNecessaryDirectoriesIfNotExist() throws IOException, IOException {
    FileHelper.createDirectory("temp");
    FileHelper.createDirectory("OUTPUT_CRT");
    FileHelper.createDirectory("OUTPUT_BASELINKER");
    FileHelper.createDirectory("STOCK_FILES");
  }

  private void listFilesAndDirectories() {
    // Get the current working directory
    log.debug("Listing All files and directories");
    String currentDir = System.getProperty("user.dir");
    log.debug("Current directory: " + currentDir);

    int counter = 0;

    File directory = new File(currentDir);

    File[] files = directory.listFiles();

    for (File file : files) {
      if (file.isDirectory()) {
        if (file.getName().contains("OUTPUT_BASELINKER") || file.getName().contains("OUTPUT_CRT")
            || file.getName().contains("tmp")) {
          counter++;
        }
        log.debug("Directory: " + file.getName());
      } else {
        log.debug("File: " + file.getName());
      }
    }
  }

  private void checkProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    if (ArrayUtils.contains(activeProfiles, "dev")) {
      log.info("Dev profile is active.");

    } else if (ArrayUtils.contains(activeProfiles, "prod")) {
      log.info("Prod profile is active.");
    }
  }

  @Override
  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    checkProfile();
    listFilesAndDirectories();
    createNecessaryDirectoriesIfNotExist();
    checkDatabaseConnection();
  }
}