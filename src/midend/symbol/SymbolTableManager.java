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
        currentTable.addSonTable(table);
        currentTable = table;
    }
    public static void addSymbol(Symbol s){
        currentTable.addSymbol(s);
    }
    public static void gotoFatherTable(){
        currentTable = currentTable.getFatherTable();
    }
    public static Symbol getSymbol(String name){
        return currentTable.getSymbol(name);
    }
    public static String getSymbolPrints(){
        return rootTable.toString();
    }
    public static boolean isGlobal(){
        return rootTable == currentTable;
    }
    public static void gotoNextSonTable(){
        currentTable = currentTable.GetNextSonTable();
    }
    public static int getCurrentTableId(){
        return currentTable.getTableId();
    }

    public static Symbol GetSymbolFromFather(String name) {
        SymbolTable table = currentTable.getFatherTable();
        while (table != null) {
            Symbol symbol = table.getSymbol(name);
            if (symbol != null) {
                return symbol;
            }
            table = table.getFatherTable();
        }
        return null;
    }
}
