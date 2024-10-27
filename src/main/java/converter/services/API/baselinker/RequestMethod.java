package converter.services.API.baselinker;

public enum RequestMethod {
  getOrders {
    @Override
    public String toString() {
      return "getOrders";
    }
  },
  setOrderFields {
    @Override
    public String toString() {
      return "setOrderFields";
    }
  },
  getInventoryProductsStock {
    @Override
    public String toString() {
      return "getInventoryProductsStock";
    }
  },
  updateInventoryProductsStock {
    @Override
    public String toString() {
      return "updateInventoryProductsStock";
    }
  },
  getInventoryProductsList {
    @Override
    public String toString() {
      return "getInventoryProductsList";
    }
  };
}
