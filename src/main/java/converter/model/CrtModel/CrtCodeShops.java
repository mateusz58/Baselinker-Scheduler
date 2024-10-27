package converter.model.CrtModel;

public enum CrtCodeShops {
  ZCRPPT {
    @Override
    public String toString() {
      return "ZCRPPT";
    }
  },
  ZCRPSW {
    @Override
    public String toString() {
      return "ZCRPSW";
    }
  },
  ZCRPMY {
    @Override
    public String toString() {
      return "ZCRPMY";
    }
  },
  ZCRPDM {
    @Override
    public String toString() {
      return "ZCRPDM";
    }
  },
  DEFAULT {
    @Override
    public String toString() {
      return "D";
    }
  };
}
