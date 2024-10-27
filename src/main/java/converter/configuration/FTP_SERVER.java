package converter.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class FTP_SERVER {

  public static String HOST;
  public static int PORT;
  public static String USER;
  public static String PASSWORD;
  public static String REMOTE_LOCATION_IN_ORDERS;
  public static String REMOTE_LOCATION_OUT_ORDERS;

  public static String REMOTE_LOCATION_STOCK;

  @Value("${ftp.remoteLocation.stocks}")
  public static void setRemoteLocationStock(String remoteLocationStock) {
    REMOTE_LOCATION_STOCK = remoteLocationStock;
  }

  @Value("${ftp.host}")
  public void setHOST(final String HOST) {
    this.HOST = HOST;
  }

  @Value("${ftp.port}")
  public void setPORT(final int PORT) {
    this.PORT = PORT;
  }

  @Value("${ftp.user}")
  public void setUSER(final String USER) {
    this.USER = USER;
  }

  @Value("${ftp.password}")
  public void setPASSWORD(final String PASSWORD) {
    this.PASSWORD = PASSWORD;
  }

  @Value("${ftp.remoteLocation}")
  public void setREMOTE_LOCATION_IN_ORDERS(final String REMOTE_LOCATION) {
    this.REMOTE_LOCATION_IN_ORDERS = REMOTE_LOCATION;
  }

  @Value("${ftp.remoteLocation.out}")
  public void setREMOTE_LOCATIONOut(final String REMOTE_LOCATION) {
    this.REMOTE_LOCATION_OUT_ORDERS = REMOTE_LOCATION;
  }
}
