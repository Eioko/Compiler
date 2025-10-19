package frontend.ast.func;

import frontend.ast.Node;
import frontend.lexer.Token;
import frontend.lexer.TokenType;

/**
 * FuncType -> 'void' | 'int'
 */
public class FuncType extends Node {
    private Token typeToken; // 'void' 或 'int'

    public FuncType(Token typeToken) {
        this.typeToken = typeToken;
    }

    public TokenType getTokenType() {
        return typeToken.getTokenType();
    }
}