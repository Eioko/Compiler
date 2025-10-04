package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * Block -> '{' { Decl | Stmt } '}'
 * items 中元素顺序即源代码顺序。
 * 每个元素要么是某个声明节点(如 ConstDecl / VarDecl)，要么是 Stmt (或其子类)。
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

    /**
     * 工具方法：判断第 idx 个是不是语句
     */
    public boolean isStmt(int idx) {
        return items.get(idx) instanceof Stmt;
    }

    /**
     * 工具方法：判断第 idx 个是不是声明
     */
    public boolean isDecl(int idx) {
        Node n = items.get(idx);
        return !(n instanceof Stmt);
    }
}