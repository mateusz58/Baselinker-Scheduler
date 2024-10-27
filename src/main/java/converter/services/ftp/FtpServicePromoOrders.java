package converter.services.ftp;

import converter.configuration.FTP_SERVER;
import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import converter.helper.FileHelper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FtpServicePromoOrders {

  private static final Logger log = LoggerFactory.getLogger(FtpServicePromoOrders.class);
  private static final String orderConstant = "ORDER_";
  private static final String regexOrderConstant = "ORDER_[A-Z 0-9-]+\\.xml";
  private final FtpServiceInterface ftp;
  private final String remotePath;
  private final int port;
  private final String password;
  private final String host;
  private final String user;
  private List<String> uploadedOrderNumbersList = new LinkedList<>();

  public FtpServicePromoOrders() {
    this.port = FTP_SERVER.PORT;
    this.password = FTP_SERVER.PASSWORD;
    this.host = FTP_SERVER.HOST;
    this.remotePath = FTP_SERVER.REMOTE_LOCATION_IN_ORDERS;
    this.user = FTP_SERVER.USER;
    ftp = FtpServiceImpl.builder()
        .server(host)
        .port(port)
        .password(password)
        .user(user)
        .build();
  }

  public FtpServicePromoOrders(String host, int port, String password, String user,
      String remotePath) {
    this.port = port;
    this.password = password;
    this.host = host;
    this.remotePath = remotePath;
    this.user = user;
    ftp = FtpServiceImpl.builder()
        .server(host)
        .port(port)
        .password(password)
        .user(user)
        .build();
  }

  private void upload(String localDirectoryPath, String remotePath) throws FtpServiceException {
    uploadedOrderNumbersList.clear();
    try {
      ftp.open();
      List<String> files = FileHelper.listAllFilesFromDirectory(localDirectoryPath);
      for (String file : files) {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
          String uploadedFileName = ftp.upload(Paths.get(localDirectoryPath, file).toString(),
              Paths.get(remotePath, file).toString());
          uploadedOrderNumbersList.add(extractOrderNumberFromFileName(uploadedFileName));
        } else {
          String fileNamePathToUpload = String.format("%s\\%s", localDirectoryPath, file);
          String uploadedFileName = ftp.upload(fileNamePathToUpload, remotePath + file);
          uploadedOrderNumbersList.add(extractOrderNumberFromFileName(uploadedFileName));
        }
      }
    } catch (Exception e) {
      throw new FtpServiceException(e.getMessage());
    }
  }

  public void uploadOrder(String localDirectoryPath, String remotePath) throws FtpServiceException {
    upload(localDirectoryPath, remotePath);
  }

  public void uploadOrder(String localDirectoryPath) throws FtpServiceException {
    upload(localDirectoryPath, remotePath);
  }

  public Collection<String> listAllOrderNumbersFromRemotePath()
      throws FtpServiceException, IOException, ServiceOperationException {
    ftp.open();
    return ftp.listFiles(remotePath).stream().filter(s -> s.matches(regexOrderConstant))
        .map(s -> s.replace(orderConstant, "").replace(".xml", "")).collect(Collectors.toList());
  }

  private String extractOrderNumberFromFileName(String fileName) {
    return fileName.replace("ORDER_", "").replace(".xml", "");
  }

  public Collection<String> listAllOrderNumbersFromRemotePath(String[] remotePaths)
      throws FtpServiceException, IOException, ServiceOperationException {
    Set<String> orderNumbers = new HashSet<>();
    ftp.open();
    for (String path : remotePaths) {
      orderNumbers.addAll(ftp.listFiles(path));
    }
    return orderNumbers.stream().filter(s -> s.matches(regexOrderConstant))
        .map(s -> s.replace(orderConstant, "").replaceAll("\\.xml", ""))
        .collect(Collectors.toList());
  }

  public void deleteAllFilesFromRemotePath() throws FtpServiceException, IOException {
    deleteAllFiles(remotePath);
  }

  public void deleteAllFilesFromRemotePath(String remotePath)
      throws FtpServiceException, IOException {
    deleteAllFiles(remotePath);
  }

  private void deleteAllFiles(String remotePath) throws FtpServiceException, IOException {
    try {
      ftp.open();
      ftp.deleteAllFilesFromRemotePathDirectory(remotePath);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new FtpServiceException(e.getMessage());
    }
  }

  public List<String> getUploadedOrderNumbersList() {
    return uploadedOrderNumbersList;
  }
}
