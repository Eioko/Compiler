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
        //这里要不要depth--（可以加一个变量一起表示）
    }
    public static Symbol getSymbol(String name, int line){
        Symbol s = currentTable.getSymbol(name);
        return s;
    }
    public static String getSymbolPrints(){
        return rootTable.toString();
    }
}
