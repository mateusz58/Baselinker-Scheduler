package converter.services.API.baselinker.stocks;

public enum RequestParameters {

  inventory_id {
    @Override
    public String toString() {
      return "inventory_id";
    }
  },
  products {
    @Override
    public String toString() {
      return "products";
    }
  },
  page {
    @Override
    public String toString() {
      return "page";
    }
  }
}
