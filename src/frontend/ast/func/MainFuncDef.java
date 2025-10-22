package frontend.ast.func;

import error.SysyError;
import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;

import java.util.ArrayList;

import static error.ErrorManager.addError;
import static error.ErrorManager.errors;
import static error.ErrorType.MISSING_RETURN_IN_NONVOID;

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
        missReturn();
        boolean inFunc = true;
        int line = mainToken.getLineNum();
        curFuncSymbol = new FuncSymbol("main", SymbolType.INTFUNC, line, new ArrayList<>());
        SymbolType symbolType = SymbolType.INTFUNC;
        this.block.check(inFunc, symbolType);
        SymbolTableManager.gotoFatherTable();
    }
    public void missReturn(){
        Node a = block.getLast();
        int lineNum = this.block.getRbraceToken().getLineNum();
        if(a == null){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
            return;
        }
        if(a instanceof Decl){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
            return;
        }
        Stmt b = (Stmt) a;
        if(!b.isReturn()){
            addError(new SysyError(MISSING_RETURN_IN_NONVOID, lineNum));
        }
    }
}