package midend.symbol;

public class Symbol {
    private String symbolName;
    private SymbolType symbolType;
    private int lineNum;
    public Symbol(String symbolName, SymbolType symbolType, int lineNum) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
        this.lineNum = lineNum;
    }
    public String getSymbolName() {
        return symbolName;
    }
    public SymbolType getSymbolType() {
        return symbolType;
    }
    public int getLineNum() {
        return lineNum;
    }
    public boolean isConst() {
        if(symbolType == SymbolType.CONSTINT || symbolType == SymbolType.CONSTINTARRAY){
            return true;
        }
        return false;
    }
}
