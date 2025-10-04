package frontend.ast.exp;

import frontend.lexer.Token;

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
}