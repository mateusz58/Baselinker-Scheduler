package converter.services.API.baselinker;

public enum HeaderKeys {
  XBLTOKEN {
    @Override
    public String toString() {
      return "X-BLToken";
    }
  },
  ContentType {
    @Override
    public String toString() {
      return "Content-Type";
    }
  };
}
