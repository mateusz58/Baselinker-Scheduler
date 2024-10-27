package converter.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class FILES {

  public static String OUTPUT_DIRECTORY_BASELINKER;
  public static String OUTPUT_DIRECTORY_CRT;
  public static String STOCK_DIRECTORY;

  @Value("${stock.directory}")
  public void setSTOCK_DIRECTORY(final String STOCK_DIRECTORY) {
    this.STOCK_DIRECTORY = STOCK_DIRECTORY;
  }

  @Value("${CRT.files.output_directory}")
  public void setOutputDirectoryCrt(final String outputDirectoryCrt) {
    this.OUTPUT_DIRECTORY_CRT = outputDirectoryCrt;
  }

  @Value("${baselinker.files.output_directory}")
  public void setBaselinkerDirectoryOutput(final String outputDirectoryBaselinker) {
    this.OUTPUT_DIRECTORY_BASELINKER = outputDirectoryBaselinker;
  }
}
