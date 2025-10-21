package frontend.ast.exp;

import frontend.ast.Node;
import frontend.lexer.Token;

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
}
