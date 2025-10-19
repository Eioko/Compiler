package midend.symbol;

public class SymbolTableManager {
    public static SymbolTable rootTable;
    public static SymbolTable currentTable;

    static int depth;
    public static void init(){
        depth = 1;
        rootTable = new SymbolTable(depth, null);
        currentTable = rootTable;
    }
    public static void createSonTable(){
        depth++;
        SymbolTable table = new SymbolTable(depth, currentTable);
        currentTable.AddSonTable(table);
        currentTable = table;
    }
    public static void addSymbol(Symbol s, int line){
        currentTable.AddSymbol(s, line);
    }
    public static void gotoFatherTable(){
        currentTable = currentTable.GetFatherTable();
    }
}
