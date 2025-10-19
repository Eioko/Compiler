package midend.symbol;

public enum SymbolType {
    CONSTINT("ConstInt"),
    CONSTINTARRAY("ConstIntArray"),
    STATICINT("StaticInt"),
    INT("Int"),
    INTARRAY("IntArray"),
    STATICINTARRAY("StaticIntArray"),
    VOIDFUNC("VoidFunc"),
    INTFUNC("IntFunc");

    private final String name;
    private SymbolType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
