package converter.services.API.baselinker;

public enum HeaderValues {

  URLEncoded {
    @Override
    public String toString() {
      return "application/x-www-form-urlencoded";
    }
  };
}
