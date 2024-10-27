package converter.services.CRT;

import converter.configuration.FILES;
import converter.exceptions.ServiceOperationException;
import converter.mapper.crt.CrtJsonToPromo;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.promoModel.OrderPromo;
import converter.services.FileTranslation;
import java.nio.file.Path;
import java.util.List;

public class CrtFileTranslationToPromo extends FileTranslation<OrderCrtJson, OrderPromo> {

  private static final String outputDirectory = FILES.OUTPUT_DIRECTORY_CRT;

  public CrtFileTranslationToPromo() throws ServiceOperationException {
    super(outputDirectory, new CrtJsonToPromo());
  }

  public static String getOutputDirectory() {
    return outputDirectory;
  }

  public void executeTask(List<?> inputObjects) throws ServiceOperationException {
    super.execute((List<OrderCrtJson>) inputObjects, "ORDER", "tbIdOrderData");
  }

  public void executeTask(List<?> inputObjects, Path output) throws ServiceOperationException {
    super.execute((List<OrderCrtJson>) inputObjects, "ORDER", "tbIdOrderData", output);
  }
}
