package converter.services.API.baselinker;

import converter.configuration.Baselinker.BASELINKER;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaderMaps {

  public static final Map<String, List<String>> headerMap = new HashMap<>();

  static {
    headerMap.put(HeaderKeys.ContentType.toString(),
        Collections.singletonList(HeaderValues.URLEncoded.toString()));
    headerMap.put(HeaderKeys.XBLTOKEN.toString(), Collections.singletonList(BASELINKER.TOKEN));
  }
}
