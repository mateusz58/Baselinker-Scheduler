package converter.exceptions;

public class FtpServiceException extends Exception {

  public FtpServiceException() {
    super();
  }

  public FtpServiceException(String message) {
    super(message);
  }

  public FtpServiceException(Throwable cause) {
    super(cause);
  }

  public FtpServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
