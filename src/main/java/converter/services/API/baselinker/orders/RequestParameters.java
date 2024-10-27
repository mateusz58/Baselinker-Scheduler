package converter.services.API.baselinker.orders;

public enum RequestParameters {

  orderId {
    @Override
    public String toString() {
      return "order_id";
    }
  },
  dateConfirmedFrom {
    @Override
    public String toString() {
      return "date_confirmed_from";
    }
  },
  dateFrom {
    @Override
    public String toString() {
      return "date_from";
    }
  },
  idFrom {
    @Override
    public String toString() {
      return "id_from";
    }
  },
  filterOrderSource {
    @Override
    public String toString() {
      return "filter_order_source";
    }
  },
  filterEmail {
    @Override
    public String toString() {
      return "filter_email";
    }
  }
}
