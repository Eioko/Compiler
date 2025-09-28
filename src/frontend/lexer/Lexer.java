package frontend.lexer;

public class Lexer {
    private static final Lexer instance = new Lexer();
    private Lexer() {};
    public static Lexer getInstance() {
        return instance;
    }
    private int lineNum = 1;

}
