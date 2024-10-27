package converter.services.ftp;

import converter.exceptions.FtpServiceException;
import converter.exceptions.ServiceOperationException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface FtpServiceInterface {

  void open() throws IOException, ServiceOperationException, FtpServiceException;

  void download(String source, String destination) throws IOException, FtpServiceException;

  String upload(String source, String destination) throws IOException, FtpServiceException;

  List<String> listFiles(String path) throws IOException, FtpServiceException;

  List<String> listFiles(String folderPath, String regex, LocalDateTime timestamp,
      Duration duration)
      throws IOException, FtpServiceException;

  List<String> deleteAllFilesFromRemotePathDirectory(String remoteFilePath)
      throws IOException, FtpServiceException;

  void close() throws IOException;
}


