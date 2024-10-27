package converter.services.ftp;

import converter.exceptions.FtpServiceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Builder;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServiceImpl implements FtpServiceInterface {

  private static final Logger log
      = LoggerFactory.getLogger(FtpServiceImpl.class);
  private final String server;
  private final String user;
  private final String password;
  private final int port;
  private FTPClient ftp;

  @Builder
  public FtpServiceImpl(String server, int port, String user, String password) {
    this.server = server;
    this.port = port;
    this.user = user;
    this.password = password;
  }

  public void open() throws IOException, FtpServiceException {
    ftp = new FTPClient();
    ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    ftp.connect(server, port);
    int reply = ftp.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      log.error("Exception in connecting to FTP Server with reply code {}", ftp.getReplyCode());
      throw new FtpServiceException(
          String.format("Exception in connecting to FTP Server with reply code %s",
              ftp.getReplyCode()));
    }
    if (!ftp.login(user, password)) {
      log.error(
          "Authentication error with FTP server username or password incorrect with Reply code {}",
          ftp.getReplyCode());
      ftp.disconnect();
      throw new FtpServiceException(String.format(
          "Authentication error with FTP server username or password incorrect with Reply code %s",
          ftp.getReplyCode()));
    }
  }

  public void close() throws IOException {
    if (ftp != null) {
      ftp.logout();
      ftp.disconnect();
    }
  }

  public List<String> listFiles(String folderPath, String regex, LocalDateTime timestamp,
      Duration duration)
      throws FtpServiceException {
    List<String> matchingFiles = new ArrayList<>();
    LocalDateTime threshold = timestamp.minus(duration);
    try {
      ftp.enterLocalPassiveMode();
      FTPFile[] files = ftp.mlistDir(folderPath, new FTPFileFilter() {
        @Override
        public boolean accept(FTPFile ftpFile) {
          LocalDateTime fileTime = LocalDateTime.ofInstant(ftpFile.getTimestamp().toInstant(),
              ZoneId.systemDefault());
          return ftpFile.getName().matches(regex) && fileTime.isAfter(threshold);
        }
      });
      if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        if (files != null && files.length > 0) {
          Arrays.sort(files, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile f1, FTPFile f2) {
              LocalDateTime t1 = LocalDateTime.ofInstant(f1.getTimestamp().toInstant(),
                  ZoneId.systemDefault());
              LocalDateTime t2 = LocalDateTime.ofInstant(f2.getTimestamp().toInstant(),
                  ZoneId.systemDefault());
              return t2.compareTo(t1);
            }
          });
          for (FTPFile file : files) {
            matchingFiles.add(file.getName());
          }
        }
      } else {
        throw new IOException(ftp.getReplyString());
      }
    } catch (IOException e) {
      throw new FtpServiceException(
          String.format("Error while obtaining files from remote ftp location %s",
              e.getMessage()));
    }
    return matchingFiles;
  }

  public List<String> listFiles(String remoteFilePath) throws FtpServiceException {
    if (remoteFilePath == null) {
      throw new IllegalArgumentException("Provided argument is null");
    }
    FTPFile[] listFtpFiles = new FTPFile[0];
    try {
      listFtpFiles = ftp.mlistDir(remoteFilePath);
      if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        log.info("Ftp listing status {}", ftp.getReplyString());
      } else {
        throw new FtpServiceException(ftp.getReplyString());
      }
    } catch (IOException e) {
      log.error("Error while obtaining files from remote ftp location {}", ftp.getReplyString());
      throw new FtpServiceException(
          String.format("Error while obtaining files from remote ftp location %s",
              ftp.getReplyString()));
    }
    return Arrays.stream(listFtpFiles)
        .map(s -> s.getName())
        .collect(Collectors.toList());
  }

  public List<String> deleteAllFilesFromRemotePathDirectory(String remoteDirectoryPath)
      throws IOException, FtpServiceException {
    if (remoteDirectoryPath == null) {
      throw new IllegalArgumentException("Provided argument is null");
    }
    FTPFile[] files = new FTPFile[0];
    try {
      files = ftp.listFiles(remoteDirectoryPath);
      for (FTPFile file : files) {
        if (Pattern.compile("[\\w]+").matcher(file.getName()).find()) {
          if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            if (!ftp.deleteFile(Paths.get(remoteDirectoryPath, file.getName()).toString())) {
              String replyString = ftp.getReplyString();
              log.error(String.format("Unsuccessful delete of file %s with error reply message %s",
                  remoteDirectoryPath, replyString));
              throw new FtpServiceException(
                  String.format("Unsuccessful delete of file %s with error reply message %s",
                      remoteDirectoryPath, replyString));
            }
          } else {
            String remoteFilePathToDelete = String.format("%s%s", remoteDirectoryPath,
                file.getName());
            if (!ftp.deleteFile(remoteFilePathToDelete)) {
              String replyString = ftp.getReplyString();
              log.error(String.format("Unsuccessful delete of file %s with error reply message %s",
                  remoteDirectoryPath, replyString));
              throw new FtpServiceException(
                  String.format("Unsuccessful delete of file %s with error reply message %s",
                      remoteDirectoryPath, replyString));
            }
          }
        }
      }
    } catch (IOException e) {
      log.error("Error while deleting files from remote path {}", e.getMessage());
      throw new FtpServiceException();
    }
    return Arrays.stream(files)
        .map(FTPFile::getName)
        .collect(Collectors.toList());
  }

  public String upload(String localFilePath, String remoteFilePath)
      throws IOException, FtpServiceException {
    if (localFilePath == null ^ remoteFilePath == null) {
      log.error("Passed argument is null");
      throw new IllegalArgumentException();
    }
    File localFile = new File(localFilePath);
    try (InputStream inputStream = new FileInputStream(localFile)) {
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
      ftp.enterLocalPassiveMode();
      if (!ftp.storeFile(remoteFilePath, inputStream)) {
        log.error(String.format(
            "Unsuccessful upload of file %s to remote file path %s with error reply message %s",
            localFilePath, remoteFilePath, ftp.getReplyString()));
        throw new FtpServiceException(String.format(
            "Unsuccessful upload of file %s to remote file path %s with error reply message %s",
            localFilePath, remoteFilePath, ftp.getReplyString()));
      }
      return Paths.get(remoteFilePath).getFileName().toString();
    } catch (IOException e) {
      log.error("Exception occured while uploading files to ftp {}", e.getMessage());
      throw new FtpServiceException();
    }
  }

  public void download(String source, String destination) throws FtpServiceException {
    if (source == null ^ destination == null) {
      log.error("Passed argument is null");
      throw new IllegalArgumentException();
    }
    try (FileOutputStream out = new FileOutputStream(destination)) {
      if (!ftp.retrieveFile(source, out)) {
        throw new FtpServiceException(
            String.format("Error while retrieving file %s to destination %s from ftp %s", source,
                destination, ftp.getReplyString()));
      }
    } catch (IOException | FtpServiceException e) {
      log.error("Error while downloading file from ftp {}", e.getMessage());
      throw new FtpServiceException();
    }
  }
}
