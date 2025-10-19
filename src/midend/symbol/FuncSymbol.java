package midend.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private final ArrayList<ValSymbol> params;

    public FuncSymbol(String funcName, SymbolType symbolType, ArrayList<ValSymbol> params) {
        super(funcName, symbolType);
        this.params = params;
    }
}