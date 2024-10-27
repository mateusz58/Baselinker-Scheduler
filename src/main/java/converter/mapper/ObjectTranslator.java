package converter.mapper;

import java.text.ParseException;
import java.util.List;

public class ObjectTranslator {

  public static <X, Y> Y translate(Mapper<X, Y> mapper, X object) throws ParseException {
    return mapper.map(object);
  }

  public static <X, Y> List<Y> translate(Mapper<X, Y> mapper, List<X> objectList)
      throws ParseException {
    return mapper.map(objectList);
  }
}
