package midend.symbol;

import error.ErrorType;
import error.SysyError;

import java.util.ArrayList;
import java.util.Hashtable;

import static error.ErrorManager.addError;

public class SymbolTable {
    /** 符号表的深度 */
    private final int depth;

    /** buildIr用于遍历子符号表 */
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
    public void addSonTable(SymbolTable symbolTable) {
        this.sonTables.add(symbolTable);
    }

    public SymbolTable GetNextSonTable() {
        return this.sonTables.get(++index);
    }

    public ArrayList<SymbolTable> GetSonTables() {
        return this.sonTables;
    }

    public void addSymbol(Symbol symbol) {
        String symbolName = symbol.getSymbolName();
        if (!this.symbolTable.containsKey(symbolName)) {
            this.symbolList.add(symbol);
            this.symbolTable.put(symbolName, symbol);
        } else {  // 当前层有相同名，重定义
            SysyError e =  new SysyError(ErrorType.REDEFINED_IDENTIFIER, symbol.getLineNum());
            addError(e);
        }
    }

    public SymbolTable getFatherTable() {
        return this.fatherTable;
    }

    private Symbol findInCurrentTable(String name) {
        return this.symbolTable.get(name);
    }
    public Symbol getSymbol(String name) {
        SymbolTable table = this;
        while (table != null) {
            Symbol symbol = table.findInCurrentTable(name);
            if (symbol != null) {
                return symbol;
            }
            table = table.getFatherTable();
        }
        return null;
    }
    public ArrayList<String> printSymbolTable() {
        ArrayList<String> list = new ArrayList<>();
        for(Symbol symbol : this.symbolList) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(depth);
            stringBuilder.append(" ");
            stringBuilder.append(symbol.getSymbolName());
            stringBuilder.append(" ");
            stringBuilder.append(symbol.getSymbolType());

            String s = stringBuilder.toString();
            list.add(s);
        }
        return list;
    }
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : this.symbolList) {
            stringBuilder.append(this.depth + " " + symbol.getSymbolName() +" "+ symbol.getSymbolType() + "\n");
        }

        for (SymbolTable sonTable : this.sonTables) {
            stringBuilder.append(sonTable.toString());
        }

        return stringBuilder.toString();
    }
    public int getTableId() {
        return this.depth;
    }

}