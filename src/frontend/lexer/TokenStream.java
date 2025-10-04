package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens;
    private int index = 0;

    public TokenStream(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
    public ArrayList<Token> getTokens() {
        return tokens;
    }
    public Token getToken(int index) {
        if(index < 0 || index >= tokens.size()){
            return null;
        }
        return tokens.get(index);
    }

    public Token getNextToken() {
        if(index >= tokens.size()){
            return null;
        }
        return tokens.get(index++);
    }

    public Token getCurrentToken() {
        if(index >= tokens.size()){
            return null;
        }
        return tokens.get(index);
    }

    public Token peek(int steps){
        if(index + steps >= tokens.size()){
            return null;
        }
        return tokens.get(index);
    }
}
