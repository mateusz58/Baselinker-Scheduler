package converter.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.rmi.ConnectException;
import org.junit.jupiter.api.Test;

public class ExceptionsTest {

  void functionTest() throws Exception {
    try {
      throw new FtpServiceException("test");
    } catch (FtpServiceException e) {
      e.printStackTrace();
      System.out.println("Caught");
      throw new ConnectException("test");
    } finally {
      System.out.println("finally");
    }
  }

  @Test
  void test() throws Exception {
    assertThrows(ConnectException.class, () -> functionTest());
  }
}
