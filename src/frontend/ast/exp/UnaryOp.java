package frontend.ast.exp;

import frontend.ast.Node;
import frontend.lexer.Token;
import frontend.lexer.TokenType;

/**
 UnaryOp → '+' | '−' | '!'
 */
public class UnaryOp extends Node {
    private Token op;
    public UnaryOp(Token op) {
        this.op = op;
    }
    public Token getOp() {
        return op;
    }
    public void check() {}
    public boolean isPlus() {
        return op.getTokenType() == TokenType.PLUS;
    }
    public boolean isMinus() {
        return op.getTokenType() == TokenType.MINU;
    }
    public boolean isNot() {
        return op.getTokenType() == TokenType.NOT;
    }
}
