package frontend.ast.func;

import frontend.ast.Node;
import frontend.lexer.Token;

/**
 * FuncFParam -> BType Ident ['[' ']']
 */
public class FuncFParam extends Node {
    private Token intToken;   // 'int'
    private Token identToken;   // 标识符
    private Token lbrackToken;  // '['  可为 null
    private Token rbrackToken;  // ']'  可为 null

    public FuncFParam(Token intToken,
                     Token identToken,
                     Token lbrackToken,
                     Token rbrackToken) {
        this.intToken = intToken;
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.rbrackToken = rbrackToken;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public boolean isArrayParam() {
        return lbrackToken != null && rbrackToken != null;
    }

    public Token getLbrackToken() {
        return lbrackToken;
    }

    public Token getRbrackToken() {
        return rbrackToken;
    }
}