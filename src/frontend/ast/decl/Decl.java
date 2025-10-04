package frontend.ast.decl;
/*
Decl → ConstDecl | VarDecl
 */
public class Decl {
    private ConstDecl constDecl;
    private VarDecl varDecl;

    private int utype;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
        utype = 0;
    }
    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
        utype = 1;
    }
}
