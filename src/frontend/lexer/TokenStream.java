package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens;
    private int index;

    public TokenStream(ArrayList<Token> tokens) {
        this.tokens = tokens;
        index = 0;
    }
    public Token getToken(int index) {
        if(index < 0 || index >= tokens.size()){
            return null;
        }
        return tokens.get(index);
    }

    public Token getCurTokenAndGo() {
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
        return tokens.get(index+steps);
    }

    public Token previous(){
        if(index > 0){
            return tokens.get(index-1);
        }
        return null;
    }

    public boolean isParseEnd(){
        return index >= tokens.size();
    }
}
