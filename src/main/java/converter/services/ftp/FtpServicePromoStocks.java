package converter.services.ftp;

import converter.configuration.FILES;
import converter.configuration.FTP_SERVER;
import converter.exceptions.FtpServiceException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FtpServicePromoStocks {

  private static final Logger log = LoggerFactory.getLogger(FtpServicePromoStocks.class);
  private final FtpServiceInterface ftp;
  private final String remotePath;
  private final int port;
  private final String password;
  private final String host;
  private final String user;
  private final String stockDirectoryLocal;

  public FtpServicePromoStocks() {
    this.port = FTP_SERVER.PORT;
    this.password = FTP_SERVER.PASSWORD;
    this.host = FTP_SERVER.HOST;
    this.remotePath = "/htdocs/stock/";
    this.user = FTP_SERVER.USER;
    this.stockDirectoryLocal = FILES.STOCK_DIRECTORY;
    ftp = FtpServiceImpl.builder()
        .server(host)
        .port(port)
        .password(password)
        .user(user)
        .build();
  }

  public FtpServicePromoStocks(String host, int port, String password, String user,
      String remotePath, String stockDirectoryLocal) {
    this.port = port;
    this.password = password;
    this.host = host;
    this.remotePath = remotePath;
    this.user = user;
    this.stockDirectoryLocal = stockDirectoryLocal;
    ftp = FtpServiceImpl.builder()
        .server(host)
        .port(port)
        .password(password)
        .user(user)
        .build();
  }

  public void getStocks(String stocksFileKeywordName, String destFileName,
      Duration timeRangeLastStocks)
      throws FtpServiceException {
    try {
      ftp.open();
      List<String> files = ftp.listFiles(remotePath, ".*" + stocksFileKeywordName + ".*",
          LocalDateTime.now(),
          timeRangeLastStocks);
      if (files == null || files.isEmpty()) {
        throw new FtpServiceException("No " + stocksFileKeywordName + " file found in ftp server");
      }
      log.info("Found {} file in ftp server, downloading it with name {} updating stocks",
          files.get(0), destFileName);
      ftp.download(remotePath + files.get(0),
          Paths.get(stockDirectoryLocal, destFileName).toString());
      log.info("Latest {} file {} successfully obtained", stocksFileKeywordName, files.get(0));
    } catch (IOException e) {
      log.error("An error occurred while communicating with the FTP server: {}", e.getMessage());
      throw new FtpServiceException("FTP error: " + e.getMessage(), e);
    } catch (Exception e) {
      log.error("An unexpected error occurred: {}", e.getMessage());
      throw new FtpServiceException("Unexpected error: " + e.getMessage(), e);
    }
  }
}
