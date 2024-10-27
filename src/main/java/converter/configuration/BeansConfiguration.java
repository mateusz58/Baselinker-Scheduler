package converter.configuration;

import converter.database.MongoDbBaseLinkerOrders;
import converter.database.MongoDbCrt;
import converter.exceptions.ServiceOperationException;
import converter.mapper.baselinker.orders.BaseLinkerJsonToBaseLinkerNosql;
import converter.mapper.baselinker.orders.BaseLinkerNoSqlToJson;
import converter.mapper.crt.CrtJsonToNoSql;
import converter.mapper.crt.CrtNoSqlToJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.baseLinkerModel.JSON.OrderBaseLinkerJson;
import converter.services.CRT.CrtFileTranslationToPromo;
import converter.services.DatabaseServiceOrdersImpl;
import converter.services.DatabaseServiceOrdersInterface;
import converter.services.FileTranslation;
import converter.services.baselinker.BaselinkerFileTranslationToPromo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

  @Bean("BASELINKER-FILETRANSLATION")
  FileTranslation baseLinkerPromoFileTranslator() throws ServiceOperationException {
    return new BaselinkerFileTranslationToPromo();
  }

  @Bean("CRT-FILETRANSLATION")
  FileTranslation crtPromoFileTranslation() throws ServiceOperationException {
    return new CrtFileTranslationToPromo();
  }

  @Bean("BASELINKER-DATABASE-SERVICE")
  @ConditionalOnBean(MongoDbBaseLinkerOrders.class)
  DatabaseServiceOrdersInterface<OrderBaseLinkerJson> databaseServiceOrdersInterfaceBaselinker(
      MongoDbBaseLinkerOrders mongoDbBaseLinker) {
    return new DatabaseServiceOrdersImpl(mongoDbBaseLinker, new BaseLinkerNoSqlToJson(),
        new BaseLinkerJsonToBaseLinkerNosql());
  }

  @Bean("CRT-DATABASE-SERVICE")
  @ConditionalOnBean(MongoDbCrt.class)
  DatabaseServiceOrdersInterface<OrderCrtJson> databaseServiceOrdersInterfaceCrt(
      MongoDbCrt mongoDbCrt) {
    return new DatabaseServiceOrdersImpl(mongoDbCrt, new CrtNoSqlToJson(), new CrtJsonToNoSql());
  }
}
