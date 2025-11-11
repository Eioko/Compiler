package midend.symbol;

import midend.ir.value.Value;

public class Symbol {
    private String symbolName;
    private SymbolType symbolType;
    private int lineNum;
    private Value value;

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
    public void SetIrValue(Value Value) {
        this.value = Value;
    }

    public Value GetIrValue() {
        return this.value;
    }
}
