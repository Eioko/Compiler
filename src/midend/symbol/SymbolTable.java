package midend.symbol;

import java.util.ArrayList;
import java.util.Hashtable;

public class SymbolTable {
    private final int depth;
    private int index;

    private final ArrayList<Symbol> symbolList;
    private final Hashtable<String, Symbol> symbolTable;
    private final SymbolTable fatherTable;
    private final ArrayList<SymbolTable> sonTables;

    public SymbolTable(int depth, SymbolTable fatherTable) {
        this.depth = depth;
        this.index = -1;
        this.fatherTable = fatherTable;
        this.symbolTable = new Hashtable<>();
        this.symbolList = new ArrayList<>();
        this.sonTables = new ArrayList<>();
    }
    public void AddSonTable(SymbolTable symbolTable) {
        this.sonTables.add(symbolTable);
    }

    public SymbolTable GetNextSonTable() {
        return this.sonTables.get(++index);
    }

    public void AddSymbol(Symbol symbol, int line) {
        String symbolName = symbol.getSymbolName();
        if (!this.symbolTable.containsKey(symbolName)) {
            this.symbolList.add(symbol);
            this.symbolTable.put(symbolName, symbol);
        } else {  // 当前层有相同名，重定义

        }
    }
    public SymbolTable GetFatherTable() {
        return this.fatherTable;
    }

    private Symbol findInCurrentTable(String name) {
        return this.symbolTable.get(name);
    }
    public Symbol GetSymbol(String name) {
        SymbolTable table = this;
        while (table != null) {
            Symbol symbol = table.findInCurrentTable(name);
            if (symbol != null) {
                return symbol;
            }
            table = table.GetFatherTable();
        }
        return null;
    }

}