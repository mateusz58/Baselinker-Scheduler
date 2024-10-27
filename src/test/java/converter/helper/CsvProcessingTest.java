package converter.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CsvProcessingTest {

  Map<String, Integer> mapToLoad = new HashMap<>();

  String stock_complete_file = "src/test/resources/stocks/stock_complete.csv";
  String stock_diff_file = "src/test/resources/stocks/stock_diff.csv";

  @Test
  void loadCSVFunctionShouldImportDiffStocksWithoutThrowingAnyException() throws IOException {
    CsvProcessing.loadCSVToHashmap(stock_diff_file, mapToLoad, (key, value) -> key,
        Integer::parseInt);
  }

  @Test
  void loadCSVFunctionShouldImportKomplettStocksWithoutThrowingAnyException() throws IOException {
    CsvProcessing.loadCSVToHashmap(stock_complete_file, mapToLoad, (key, value) -> key,
        Integer::parseInt);
  }
}