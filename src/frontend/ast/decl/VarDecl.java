package frontend.ast.decl;

import frontend.ast.Node;
import frontend.lexer.Token;

import java.util.ArrayList;
/*
 *  VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
 **/
public class VarDecl extends Node {
    private Token staticToken;
    private BType bType;
    private VarDef varDef;
    private ArrayList<Token> commaTokens;
    private ArrayList<VarDef> varDefs;
    private Token semicnToken;
    public VarDecl(Token staticToken,
                   BType bType,
                   VarDef varDef,
                   ArrayList<Token> commaTokens,
                   ArrayList<VarDef> varDefs,
                   Token semicnToken) {
        this.staticToken = staticToken;
        this.bType = bType;
        this.varDef = varDef;
        this.commaTokens = commaTokens;
        this.varDefs = varDefs;
        this.semicnToken = semicnToken;
    }
    public void check(){
        boolean isStatic;
        if(staticToken!=null){
            isStatic = true;
        }else{
            isStatic = false;
        }
        varDef.check(isStatic);
        for(VarDef v: varDefs){
            v.check(isStatic);
        }
    }
    public void buildIr(){
        varDef.buildIr();
        for (VarDef v : varDefs) {
            v.buildIr();
        }
    }
}
