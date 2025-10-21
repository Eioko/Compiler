package frontend.ast.exp;

import frontend.lexer.Token;
import midend.symbol.SymbolType;

/**
 * Number -> IntConst
 */
public class Number extends ComptueExp {
    private Token intConstToken;

    public Number(Token intConstToken) {
        this.intConstToken = intConstToken;
    }

    public Token getIntConstToken() {
        return intConstToken;
    }
    public void check(){

    }
    public SymbolType getType() {
        return SymbolType.INT;
    }
}