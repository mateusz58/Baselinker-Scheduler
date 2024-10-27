package converter.schedulers;

import converter.exceptions.SchedulerOperationException;
import converter.helper.FileHelper;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerHelper {

  private static Logger log = LoggerFactory.getLogger(SchedulerHelper.class);

  public static Path createLocalDateTimeFolderName()
      throws IOException, SchedulerOperationException {
    Path temporaryDirectoryPath = null;
    try {
      String directoryName = LocalDateTime.now().withNano(0).toString()
          .replaceAll("[^A-Za-z0-9]", "_");
      temporaryDirectoryPath = Path.of(String.format("./temp/%s/", directoryName));
      FileHelper.createDirectory(temporaryDirectoryPath.toString());
    } catch (IOException e) {
      log.error("Error while creating directory for temporary files: {}", e.getMessage());
      throw new SchedulerOperationException(
          "Error while creating directory for temporary files: " + e.getMessage());
    }
    return temporaryDirectoryPath;
  }
}
