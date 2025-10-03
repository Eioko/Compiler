package frontend.lexer;

import error.ErrorType;
import error.SysyError;

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

    public ArrayList<Token> lexerAnalyze(String source, ArrayList<SysyError> errors) {

        int codeLen = source.length();
        source += "\0\0";
        for(readIndex = 0; readIndex < codeLen; readIndex++) {
            char c = source.charAt(readIndex);
            char d = source.charAt(readIndex+1);
            String singleChar = String.valueOf(c);
            if(c=='\n'){
                lineNum++;
            }else if(Character.isLetter(c)||c=='_'){
                handleIndetifier(source, codeLen, singleChar);
            }else if(Character.isDigit(c)){
                handleIntConst(source, codeLen, singleChar);
            }else if(c=='"'){
                handleStringConst(source, codeLen, singleChar);
            }else if(c=='!'){
                if(d == '='){
                    tokens.add(new Token("!=", TokenType.NEQ, lineNum));
                    readIndex++;
                }else{
                    tokens.add(new Token("!", TokenType.NOT, lineNum));
                }
            }else if(c=='&'){
                if(d != '&'){
                    errors.add(new SysyError(ErrorType.ILLEGAL_SYMBOL, lineNum));
                }else{
                    tokens.add(new Token("&&", TokenType.AND, lineNum));
                    readIndex++;
                }
            }else if(c=='|'){
                if(d != '|'){
                    errors.add(new SysyError(ErrorType.ILLEGAL_SYMBOL, lineNum));
                }else{
                    tokens.add(new Token("||", TokenType.OR, lineNum));
                    readIndex++;
                }
            }else if(c=='+'){
                tokens.add(new Token("+", TokenType.PLUS, lineNum));
            }else if(c=='-'){
                tokens.add(new Token("-", TokenType.MINU, lineNum));
            }else if(c=='*'){
                tokens.add(new Token("*", TokenType.MULT, lineNum));
            }else if(c=='/'){
                handleNoteOrDiv(source, codeLen);
            }else if(c=='%'){
                tokens.add(new Token("%", TokenType.MOD, lineNum));
            }else if(c=='<'){
                if(d == '='){
                    tokens.add(new Token("<=", TokenType.LEQ, lineNum));
                    readIndex++;
                }else{
                    tokens.add(new Token("<", TokenType.LSS, lineNum));
                }
            }else if(c=='>'){
                if(d == '='){
                    tokens.add(new Token(">=", TokenType.GEQ, lineNum));
                    readIndex++;
                }else{
                    tokens.add(new Token(">", TokenType.GRE, lineNum));
                }
            }else if(c=='='){
                if(d == '='){
                    tokens.add(new Token("==", TokenType.EQL, lineNum));
                    readIndex++;
                }else{
                    tokens.add(new Token("=", TokenType.ASSIGN, lineNum));
                }
            }else if(c==';'){
                tokens.add(new Token(";",TokenType.SEMICN, lineNum));
            }else if(c==',') {
                tokens.add(new Token(",", TokenType.COMMA, lineNum));
            }else if(c=='('){
                tokens.add(new Token("(", TokenType.LPARENT, lineNum));
            }else if(c==')'){
                tokens.add(new Token(")", TokenType.RPARENT, lineNum));
            }else if(c=='['){
                tokens.add(new Token("[", TokenType.LBRACK, lineNum));
            }else if(c==']'){
                tokens.add(new Token("]", TokenType.RBRACK, lineNum));
            }else if(c=='{'){
                tokens.add(new Token("{", TokenType.LBRACE, lineNum));
            }else if(c=='}'){
                tokens.add(new Token("}", TokenType.RBRACE, lineNum));
            }
        }
        return tokens;
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
    private void handleStringConst(String source, int codeLen, String startChar) {
        StringBuilder ident  = new StringBuilder(startChar);
        for(int j = readIndex+1; j < codeLen; j++) {
            char d = source.charAt(j);
            if(d =='"'){
                readIndex = j;
                ident.append(d);
                break;
            }
            ident.append(d);
        }
        String identStr = ident.toString();
        TokenType type = TokenType.STRCON;
        tokens.add(new Token(identStr, type, lineNum));
    }
    private void handleNoteOrDiv(String source, int codeLen) {
        readIndex++;

        if(readIndex<codeLen && source.charAt(readIndex)=='/'){
            readIndex++;
            while(readIndex<codeLen && source.charAt(readIndex)!='\n'){
                readIndex++;
            }
            if(readIndex<codeLen){
                lineNum++;
            }
        }else if(readIndex<codeLen && source.charAt(readIndex)=='*'){
            readIndex++;
            while(readIndex<codeLen){
                while(readIndex<codeLen && source.charAt(readIndex)!='*'){
                    char d = source.charAt(readIndex);
                    if(d == '\n') {
                        lineNum++;
                    }
                    readIndex++;
                }
                while(readIndex<codeLen && source.charAt(readIndex)=='*'){
                    readIndex++;
                }
                if(readIndex<codeLen && source.charAt(readIndex)=='/'){
                    return;
                }
            }
        }else{
            tokens.add(new Token("/", TokenType.DIV, lineNum));
            readIndex--;
        }
    }
}
