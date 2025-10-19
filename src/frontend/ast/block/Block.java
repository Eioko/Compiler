package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.decl.Decl;
import frontend.ast.func.FuncType;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;
import midend.symbol.SymbolType;

import java.util.ArrayList;

/**
 * Block -> '{' { Decl | Stmt } '}'
 */
public class Block extends Node {
    private Token lbraceToken;            // '{'
    private ArrayList<Node> items;        // 依次放 Decl 或 Stmt
    private Token rbraceToken;            // '}'

    public Block(Token lbraceToken,
                 ArrayList<Node> items,
                 Token rbraceToken) {
        this.lbraceToken = lbraceToken;
        this.items = items;
        this.rbraceToken = rbraceToken;
    }

    public Token getLbraceToken() {
        return lbraceToken;
    }

    public ArrayList<Node> getItems() {
        return items;
    }

    public Token getRbraceToken() {
        return rbraceToken;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public boolean isStmt(int idx) {
        return items.get(idx) instanceof Stmt;
    }

    public boolean isDecl(int idx) {
        Node n = items.get(idx);
        return !(n instanceof Stmt);
    }

    public Node getLast() {
        return items.get(items.size() - 1);
    }
    public void check(boolean inFunc, SymbolType funcType){
        for (Node n : items) {
            if (n instanceof Stmt) {
                Stmt stmt = (Stmt) n;
                stmt.check();
            }else{
                Decl decl = (Decl) n;
                decl.check();
            }
        }
    }
}