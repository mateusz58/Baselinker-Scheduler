package converter.exceptions;

public class SchedulerOperationException extends Exception {

  public SchedulerOperationException() {
    super();
  }

  public SchedulerOperationException(String message) {
    super(message);
  }

  public SchedulerOperationException(Throwable cause) {
    super(cause);
  }

  public SchedulerOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
