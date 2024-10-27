package converter.ftp;

import static org.junit.jupiter.api.Assertions.assertThrows;

import converter.exceptions.FtpServiceException;
import converter.services.ftp.FtpServicePromoOrders;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class FtpTestsExceptions {

  private static final String path = "src/test/resources/FTP/BASELINKER/ORDERS/";
  private static String host = "www.mkwk019.cba.pl";
  private static String remotePath = "/iweriuhweiur123.cba.pl/testing/";
  private static String password = "$Status1$";
  private static String user = "testing123";
  private static int port = 21;
  private static FtpServicePromoOrders ftpServicePromoOrders;

  @BeforeAll
  static void setUp() {
    ftpServicePromoOrders = new FtpServicePromoOrders(host, port, "INVALID_PASSWORD",
        "INVALID_USER", remotePath);
  }

  @Test
  void shouldThrowFtpServiceExceptionWhenInvalidCredentialsAreProvided()
      throws IOException, FtpServiceException {
    ftpServicePromoOrders = new FtpServicePromoOrders(host, port, "INVALID_PASSWORD",
        "INVALID_USER", remotePath);
    assertThrows(FtpServiceException.class,
        () -> ftpServicePromoOrders.uploadOrder("src/test/resources/FTP/BASELINKER/ORDERS/123"),
        "Invalid credentials");
  }
}