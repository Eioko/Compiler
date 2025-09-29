package frontend.lexer;

import java.util.ArrayList;
import java.util.Map;

public class Lexer {
    private static final Lexer instance = new Lexer();
    private Lexer() {}
    public static Lexer getInstance() {
        return instance;
    }
    private int lineNum = 1;
    public ArrayList<Token> tokens = new ArrayList<>();
    private int readIndex;

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("const",    TokenType.CONSTTK),
            Map.entry("int",      TokenType.INTTK),
            Map.entry("static",   TokenType.STATICTK),
            Map.entry("break",    TokenType.BREAKTK),
            Map.entry("continue", TokenType.CONTINUETK),
            Map.entry("if",       TokenType.IFTK),
            Map.entry("main",     TokenType.MAINTK),
            Map.entry("else",     TokenType.ELSETK),
            Map.entry("for",      TokenType.FORTK),
            Map.entry("return",   TokenType.RETURNTK),
            Map.entry("void",     TokenType.VOIDTK),
            Map.entry("printf",   TokenType.PRINTFTK)
    );

    public ArrayList<Token> lexerAnalyze(String source) {

        int codeLen = source.length();
        source += "\0\0";
        for(readIndex = 0; readIndex < codeLen; readIndex++) {
            char c = source.charAt(readIndex);
            String singleChar = String.valueOf(c);
            if(c=='\n'){
                lineNum++;
            }else if(Character.isLetter(c)||c=='_'){
                handleIndetifier(source, codeLen, singleChar);
            }else if(Character.isDigit(c)){
                handleIntConst(source, codeLen, singleChar);
            }else if(c=='"'){

            }
        }

    }
    private void handleIndetifier(String source, int codeLen, String startChar) {
        StringBuilder ident  = new StringBuilder(startChar);
        for(int j = readIndex+1; j < codeLen; j++) {
            char d = source.charAt(j);
            if(!(Character.isLetterOrDigit(d)||d=='_')){
                readIndex = j-1;
                break;
            }
            ident.append(d);
        }
        String identStr = ident.toString();
        TokenType type;
        type = KEYWORDS.getOrDefault(identStr, TokenType.IDENFR);
        tokens.add(new Token(identStr, type, lineNum));
    }
    private void handleIntConst(String source, int codeLen, String startChar) {
        StringBuilder ident  = new StringBuilder(startChar);
        for(int j = readIndex+1; j < codeLen; j++) {
            char d = source.charAt(j);
            if(!(Character.isDigit(d))){
                readIndex = j-1;
                break;
            }
            ident.append(d);
        }
        String identStr = ident.toString();
        TokenType type = TokenType.INTCON;
        tokens.add(new Token(identStr, type, lineNum));
    }
}
