package frontend.ast.decl;

import frontend.ast.Node;
import frontend.lexer.Token;

import java.util.ArrayList;
/*
 *  VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
 **/
public class VarDecl extends Node {
    private Token staticToken;
    private Token intToken;
    private VarDef varDef;
    private ArrayList<Token> commaTokens;
    private ArrayList<VarDef> varDefs;
    private Token semicnToken;
    public VarDecl(Token staticToken,
                   Token intToken,
                   VarDef varDef,
                   ArrayList<Token> commaTokens,
                   ArrayList<VarDef> varDefs,
                   Token semicnToken) {
        this.staticToken = staticToken;
        this.intToken = intToken;
        this.varDef = varDef;
        this.commaTokens = commaTokens;
        this.varDefs = varDefs;
        this.semicnToken = semicnToken;
    }
}
