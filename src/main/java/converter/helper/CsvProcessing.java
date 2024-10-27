package converter.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CsvProcessing {

  public static <K, V> void loadCSVToHashmap(String filename, Map<K, V> map,
      BiFunction<String, String, K> keyMapper, Function<String, V> valueMapper) throws IOException {
    map.clear();
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;
    boolean firstLine = true;
    while ((line = reader.readLine()) != null) {
      if (firstLine) {
        firstLine = false;
        continue;
      }
      String[] tokens = line.split(";");
      if (tokens.length == 2) {
        K key = keyMapper.apply(tokens[0].trim(), tokens[1].trim());
        V value = valueMapper.apply(tokens[1].trim());
        map.put(key, value);
      }
    }
    reader.close();
  }
}
