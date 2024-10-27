package converter.services.baselinker;

import converter.configuration.FILES;
import converter.exceptions.ServiceOperationException;
import converter.mapper.ObjectTranslator;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerXmlMapper;
import converter.mapper.baselinker.orders.BaseLinkerXmlToPromoXmlMapper;
import converter.model.baseLinkerModel.JSON.OrdersBaseLinkerJson;
import converter.model.baseLinkerModel.XML.OrderBaseLinkerXml;
import converter.model.baseLinkerModel.XML.OrdersBaseLinkerXml;
import converter.model.promoModel.OrderPromo;
import converter.services.FileTranslation;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;

public class BaselinkerFileTranslationToPromo extends
    FileTranslation<OrderBaseLinkerXml, OrderPromo> {

  private static final String OUTPUTDIRECTORY = FILES.OUTPUT_DIRECTORY_BASELINKER;

  public BaselinkerFileTranslationToPromo() throws ServiceOperationException {
    super(OUTPUTDIRECTORY, new BaseLinkerXmlToPromoXmlMapper());
  }

  public static String getOutputdirectory() {
    return OUTPUTDIRECTORY;
  }

  public void executeTask(List<?> list) throws ServiceOperationException, ParseException {
    List<OrdersBaseLinkerJson> ordersJson = (List<OrdersBaseLinkerJson>) list;
    List<OrdersBaseLinkerXml> orders = ObjectTranslator.translate(
        new BaseLinkerJsonToBaseLinkerXmlMapper(), ordersJson);
    for (int i = 0; i < orders.size(); i++) {
      List<OrderBaseLinkerXml> ordersBaseLinkerXml = orders.get(i).getOrders();
      super.execute(ordersBaseLinkerXml, "ORDER", "tbIdOrderData");
    }
  }

  public void executeTask(List<?> list, Path outputDirectory)
      throws ServiceOperationException, ParseException {
    List<OrdersBaseLinkerJson> ordersBaseLinkerJsons = (List<OrdersBaseLinkerJson>) list;
    List<OrdersBaseLinkerXml> orders = ObjectTranslator.translate(
        new BaseLinkerJsonToBaseLinkerXmlMapper(), ordersBaseLinkerJsons);
    for (int i = 0; i < orders.size(); i++) {
      List<OrderBaseLinkerXml> ordersBaseLinkerXml = orders.get(i).getOrders();
      super.execute(ordersBaseLinkerXml, "ORDER", "tbIdOrderData",
          Path.of(outputDirectory.toString()));
    }
  }
}
