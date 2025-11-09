package frontend.ast.decl;

import frontend.ast.Node;
import frontend.lexer.Token;

import java.util.ArrayList;
/*
ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
 */
public class ConstDecl extends Node {
    private Token constToken;
    private BType bType;
    private ConstDef constDef;
    private ArrayList<Token> commaTokens;
    private ArrayList<ConstDef> constDefs;
    private Token semicnToken;

    public ConstDecl(Token constToken,
                     BType bType,
                     ConstDef constDef,
                     ArrayList<Token> commaTokens,
                     ArrayList<ConstDef> constDefs,
                     Token semicnToken) {
        this.constToken = constToken;
        this.bType = bType;
        this.constDef = constDef;
        this.commaTokens = commaTokens;
        this.constDefs = constDefs;
        this.semicnToken = semicnToken;
    }

    public void check(){
        constDef.check();
        for(ConstDef def : constDefs){
            def.check();
        }
    }
    public void buildIr(){
        constDef.buildIr();
        for(ConstDef def : constDefs){
            def.buildIr();
        }
    }
}
