package midend.symbol;

public class Symbol {
    private String symbolName;
    private SymbolType symbolType;
    private int isConst;

    public Symbol(String symbolName, SymbolType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }
    public String getSymbolName() {
        return symbolName;
    }
    public SymbolType getSymbolType() {
        return symbolType;
    }

}
