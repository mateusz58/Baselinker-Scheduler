package converter.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NameFormatterTest {

  @Test
  void getFirstNameFunctionShouldValidate() {
    String input = "Janusz Pilarczyk";
    String expected = "Janusz";

    String actual = NameFormatter.getFirstName(input);

    assertEquals(actual, expected);
  }

  @Test
  void getLastFunctionFunctionShouldValidate() {
    String input = "Janusz Pilarczyk";
    String expected = "Pilarczyk";

    String actual = NameFormatter.getLastName(input);

    assertEquals(actual, expected);
  }
}