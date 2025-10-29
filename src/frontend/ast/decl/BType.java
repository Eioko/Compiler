package frontend.ast.decl;

import frontend.ast.Node;
import frontend.lexer.Token;

/**
 * BType → 'int'
 */
public class BType extends Node {
    private Token intToken;
    public BType(Token intToken) {
        this.intToken = intToken;
    }
}
