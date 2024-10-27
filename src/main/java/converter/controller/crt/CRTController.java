package converter.controller.crt;

import static converter.controller.crt.ControllerHelper.isAuthorized;
import static converter.controller.crt.ControllerHelper.parseJson;

import ch.qos.logback.classic.Logger;
import converter.configuration.FILES;
import converter.exceptions.DatabaseOperationException;
import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import converter.mapper.Mapper;
import converter.mapper.ObjectTranslator;
import converter.mapper.crt.CrtEventJsonToOrderCrtJsonMapper;
import converter.model.CrtModel.JSON.EventCrtJson;
import converter.model.CrtModel.JSON.OrderCrtJson;
import converter.model.CrtModel.JSON.StateOrderEnum;
import converter.services.DatabaseServiceOrdersInterface;
import converter.services.FileTranslation;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/crt")
@ConditionalOnProperty(name = "crt.controller.enabled", havingValue = "true", matchIfMissing = true)
public class CRTController {

  private static final Logger log = (Logger) LoggerFactory.getLogger(CRTController.class);
  DatabaseServiceOrdersInterface databaseServiceOrders;
  FileTranslation crtFileTranslationToPromo;
  FtpServicePromoOrders ftpServicePromoOrders;
  Mapper mapEventToOrder;

  @Value("${x-api-key}")
  private String token;

  public CRTController(
      @Qualifier("CRT-DATABASE-SERVICE") DatabaseServiceOrdersInterface databaseServiceOrders,
      @Qualifier("CRT-FILETRANSLATION") FileTranslation crtFileTranslationToPromo,
      FtpServicePromoOrders ftpServicePromoOrders) {
    this.databaseServiceOrders = databaseServiceOrders;
    this.crtFileTranslationToPromo = crtFileTranslationToPromo;
    this.ftpServicePromoOrders = ftpServicePromoOrders;
    mapEventToOrder = new CrtEventJsonToOrderCrtJsonMapper();
  }

  @PostMapping(produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> postCrtOrder(@RequestHeader HttpHeaders headerMap,
      @RequestBody(required = false) String object)
      throws DatabaseOperationException, ParseException {
    HttpHeaders temp = Optional.ofNullable(headerMap).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No headers provided"));
    if (temp.get("x-api-key") == null || !isAuthorized(temp.get("x-api-key").get(0), token)) {
      log.info("Unauthorized access detected");
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
    if (object == null) {
      log.error("Http request body is null");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to add null object.");
    }
    EventCrtJson eventCrtJson = null;
    try {
      log.info("Http request incoming with body {}", object);
      eventCrtJson = (EventCrtJson) parseJson(object, EventCrtJson.class);
      OrderCrtJson mappedOrderFromEvent = ObjectTranslator.translate(
          new CrtEventJsonToOrderCrtJsonMapper(), eventCrtJson);
      databaseServiceOrders.add(mappedOrderFromEvent);
      if (eventCrtJson.getState().equals(StateOrderEnum.fulfilled)) {
        crtFileTranslationToPromo.executeTask(List.of(mappedOrderFromEvent));
        ftpServicePromoOrders.uploadOrder(FILES.OUTPUT_DIRECTORY_CRT);
      }
      if (databaseServiceOrders.updateOrdersWhereFtpStatusIsFalseToTrueStatus(
          mappedOrderFromEvent.getOrderId()))
        ;
    } catch (IOException | ServiceOperationException | FtpServiceException e) {
      log.error("Incorrect request body.");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request body.");
    }
    return ControllerHelper.createJsonOkResponse(eventCrtJson);
  }

  @GetMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getAllCrtOrders(@RequestHeader HttpHeaders headerMap)
      throws ServiceOperationException {
    HttpHeaders temp = Optional.ofNullable(headerMap).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No headers provided"));
    if (temp.get("x-api-key") == null || !isAuthorized(temp.get("x-api-key").get(0), token)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
    return ControllerHelper.createJsonOkResponse(databaseServiceOrders.getAll());
  }
}
