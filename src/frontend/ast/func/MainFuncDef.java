package frontend.ast.func;

import error.SysyError;
import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import midend.ir.type.DataType;
import midend.ir.type.IntegerType;
import midend.ir.type.PointerType;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

import java.util.ArrayList;


/**
 * MainFuncDef -> 'int' 'main' '(' ')' Block
 */
public class MainFuncDef extends Node {
    private Token intToken;
    private Token mainToken;
    private Token lparenToken;
    private Token rparenToken;
    private Block block;

    public MainFuncDef(Token intToken,
                       Token mainToken,
                       Token lparenToken,
                       Token rparenToken,
                       Block block) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.lparenToken = lparenToken;
        this.rparenToken = rparenToken;
        this.block = block;
    }

    public Token getIntToken() {
        return intToken;
    }

    public Token getMainToken() {
        return mainToken;
    }

    public Block getBlock() {
        return block;
    }

    public void check(){
        SymbolTableManager.createSonTable();
        block.missReturn();
        int line = mainToken.getLineNum();
        curFuncSymbol = new FuncSymbol("main", SymbolType.INTFUNC, line, new ArrayList<>());
        this.block.check();
        SymbolTableManager.gotoFatherTable();
    }
    public void buildIr(){
        DataType returnType = new IntegerType();
        ArrayList<DataType> paramTypes = new ArrayList<>();

        curfunc = irBuilder.buildFunction("main", returnType, paramTypes);
        //创建函数体的基本块
        BasicBlock entryBlock = irBuilder.buildBasicBlock(curfunc);
        curBlock = entryBlock;
        SymbolTableManager.gotoNextSonTable();
        block.buildIr();
        SymbolTableManager.gotoFatherTable();
    }
}