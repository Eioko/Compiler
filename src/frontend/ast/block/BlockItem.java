package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;

/**
 * BlockItem → Decl | Stmt
 */
public class BlockItem extends Node {
    private Decl decl;
    private Stmt stmt;
    private int utype;

    public BlockItem(Decl decl) {
        this.decl = decl;
        this.utype = 0;
    }
    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
        this.utype = 1;
    }
    public boolean isDecl(){
        return utype == 0;
    }
    public Stmt getStmt(){
        return stmt;
    }
    public void check(){
        if(utype == 0){
            decl.check();
        }else if(utype == 1){
            stmt.check();
        }else{
            throw new Error("Error in BlockItem");
        }
    }
}
