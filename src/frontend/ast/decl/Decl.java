package frontend.ast.decl;

import frontend.ast.Node;

/*
Decl → ConstDecl | VarDecl
 */
public class Decl extends Node {
    private ConstDecl constDecl;
    private VarDecl varDecl;

    private final int utype;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
        utype = 0;
    }
    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
        utype = 1;
    }
    public void check(){
        if(utype == 0){
            constDecl.check();
        }else{
            varDecl.check();
        }
    }
    public void buildIr(){
        if(utype == 0){
            constDecl.buildIr();
        }else{
            varDecl.buildIr();
        }
    }
}
