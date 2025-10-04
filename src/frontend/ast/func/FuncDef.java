package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.lexer.Token;

/**
 * FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
 * 非 main 的普通函数定义
 */
public class FuncDef extends Node {
    private FuncType funcType;
    private Token identToken;
    private Token lparenToken;
    private FuncFParams funcFParams; // 可能为 null
    private Token rparenToken;
    private Block block;

    public FuncDef(FuncType funcType,
                   Token identToken,
                   Token lparenToken,
                   FuncFParams funcFParams,
                   Token rparenToken,
                   Block block) {
        this.funcType = funcType;
        this.identToken = identToken;
        this.lparenToken = lparenToken;
        this.funcFParams = funcFParams;
        this.rparenToken = rparenToken;
        this.block = block;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    public Token getLparenToken() {
        return lparenToken;
    }

    public Token getRparenToken() {
        return rparenToken;
    }
}