package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;
/*
     VarDef → Ident [ '[' ConstExp ']' ]
            | Ident [ '[' ConstExp ']' ] '=' InitVal
 */
public class VarDef extends Node {
    private Token ident;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;

    private Token assignToken;
    private InitVal initVal;

    private int utype;

    public VarDef (Token ident,
                   Token lbrack,
                   ConstExp constExp,
                   Token rbrack){
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;

        this.utype = 0;
    }
    public VarDef (Token ident,
                   Token lbrack,
                   ConstExp constExp,
                   Token rbrack,
                   Token assignToken,
                   InitVal initVal){
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assignToken = assignToken;
        this.initVal = initVal;

        this.utype = 1;
    }
}
