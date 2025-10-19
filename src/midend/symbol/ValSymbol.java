package midend.symbol;

public class ValSymbol extends Symbol {
    private int size;
    public ValSymbol(String symbolName, SymbolType symbolType, int size) {
        super(symbolName, symbolType);
        this.size = size;
    }
    public ValSymbol(String symbolName, SymbolType symbolType) {
        //这里也可以是数组（函数形参）
        super(symbolName, symbolType);
    }
}
