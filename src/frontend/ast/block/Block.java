package frontend.ast.block;

import error.SysyError;
import frontend.ast.Node;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;

import java.util.ArrayList;

import static error.ErrorManager.addError;
import static error.ErrorType.MISSING_RETURN_IN_NONVOID;

/**
 * Block → '{' { BlockItem } '}'
 */
public class Block extends Node {
    private Token lbraceToken;            // '{'
    private ArrayList<BlockItem> items;        // 依次放 Decl 或 Stmt
    private Token rbraceToken;            // '}'

    public Block(Token lbraceToken,
                 ArrayList<BlockItem> items,
                 Token rbraceToken) {
        this.lbraceToken = lbraceToken;
        this.items = items;
        this.rbraceToken = rbraceToken;
    }

    public Token getLbraceToken() {
        return lbraceToken;
    }

    public ArrayList<BlockItem> getItems() {
        return items;
    }

    public Token getRbraceToken() {
        return rbraceToken;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public BlockItem getLast() {
        if(isEmpty()) return null;
        return items.get(items.size() - 1);
    }
    //有返回值的函数缺少return语句（g错误）
    public void missReturn(){
        BlockItem a = this.getLast();
        int lineNum = this.getRbraceToken().getLineNum();
        if(a == null){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
            return;
        }
        if(a.isDecl()){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
            return;
        }
        Stmt b = a.getStmt();
        if(!b.isReturn()){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
        }
    }
    public void check(){
        for (BlockItem n : items) {
            n.check();
        }
    }
}