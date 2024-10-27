package converter.mapper;

import java.text.ParseException;
import java.util.List;

public interface Mapper<X, Y> {

  Y map(X object) throws ParseException;

  List<Y> map(List<X> object) throws ParseException;
}
