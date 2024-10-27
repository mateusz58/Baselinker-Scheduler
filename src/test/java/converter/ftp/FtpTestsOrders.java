package converter.ftp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class FtpTestsOrders {

  private static final String localPath = "src/test/resources/FTP/BASELINKER/ORDERS/";

  private static String host = "www.mkwk019.cba.pl";
  private static String remotePath = "/iweriuhweiur123.cba.pl/testing/";
  private static String password = "$Status1$";
  private static String user = "testing123";
  private static int port = 21;
  private static FtpServicePromoOrders ftpServicePromoOrders;

  @BeforeAll
  static void setUp() {
    ftpServicePromoOrders = new FtpServicePromoOrders(host, port, password, user, remotePath);
  }

  @BeforeEach
  void uploadBeforeTestStart()
      throws IOException, FtpServiceException, ServiceOperationException, InterruptedException {
    Thread.sleep(1000);
    ftpServicePromoOrders.uploadOrder(localPath);
  }

  @AfterEach
  void tearUp() throws IOException, FtpServiceException {
    ftpServicePromoOrders.deleteAllFilesFromRemotePath();
  }

  @Test
  void shouldDeleteAllFIlesFromRemoteDirectory()
      throws IOException, FtpServiceException, ServiceOperationException {
    ftpServicePromoOrders.deleteAllFilesFromRemotePath();

    assertEquals(ftpServicePromoOrders.listAllOrderNumbersFromRemotePath(),
        Collections.emptyList());
  }

  @Test
  void ftpTestForListingAllBaselinkerOrdersAndExtractingNumber()
      throws IOException, FtpServiceException, ServiceOperationException {
    ftpServicePromoOrders.uploadOrder(localPath);
    List<String> expected = List.of("BL73320721", "BL73650280", "BL73905475", "BL73941299");

    List<String> actual = (List<String>) ftpServicePromoOrders.listAllOrderNumbersFromRemotePath();

    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  void ftpListAllOrderNumbersFromRemotePath()
      throws IOException, FtpServiceException, ServiceOperationException {
    List<String> expected = new LinkedList<>();
    expected.add("BL73320721");
    expected.add("BL73650280");
    expected.add("BL73905475");
    expected.add("BL73941299");

    List<String> actual = (List<String>) ftpServicePromoOrders.listAllOrderNumbersFromRemotePath();

    Collections.sort(expected);
    Collections.sort(actual);
    assertEquals(expected, actual);
  }

  @Test
  void uploadFunctionShouldExecuteWithoutError()
      throws IOException, FtpServiceException, ServiceOperationException {
    ftpServicePromoOrders.uploadOrder(localPath);
  }
}