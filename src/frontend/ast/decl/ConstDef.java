package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;

public class ConstDef extends Node {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp;
    private Token rbrack = null;
    private Token assignToken;
    private ConstInitVal constInitVal;

    public ConstDef(Token ident,
                    Token lbrack,
                    ConstExp constExp,
                    Token rbrack,
                    Token assignToken,
                    ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assignToken = assignToken;
        this.constInitVal = constInitVal;
    }
}
