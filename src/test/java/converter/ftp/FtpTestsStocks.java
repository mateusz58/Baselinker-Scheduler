package converter.ftp;

import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import converter.services.ftp.FtpServicePromoStocks;
import java.io.IOException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class FtpTestsStocks {

  private static String host = "ftp.motion-fashion.de";
  private static String remotePath = "/htdocs/stock/";
  private static String password = "oYt73uKE";
  private static String user = "mfne_live5";

  private static String stockDirectoryLocal = "./STOCK_FILES";
  private static int port = 21;
  private static FtpServicePromoStocks ftpServicePromoStocks;

  @BeforeAll
  static void setUp() {
    ftpServicePromoStocks = new FtpServicePromoStocks(host, port, password, user, remotePath,
        stockDirectoryLocal);
  }

  @Test
  void ftpTestDifference() throws IOException, FtpServiceException, ServiceOperationException {
    ftpServicePromoStocks.getStocks("bestand_diff", "stock_diff.csv", Duration.ofHours(2L));
  }

  @Test
  void ftpTestComplete() throws IOException, FtpServiceException, ServiceOperationException {
    ftpServicePromoStocks.getStocks("bestand_komplett", "stock_complete.csv",
        Duration.ofHours(30L));
  }
}
