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
import midend.symbol.ValSymbol;

import java.util.ArrayList;

import static error.ErrorManager.addError;
import static error.ErrorType.MISSING_RETURN_IN_NONVOID;

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
    public void check(){
        String name = identToken.getTokenContent();
        int line = identToken.getLineNum();
        SymbolType symbolType;
        if(funcType.getTokenType() == TokenType.VOIDTK){
            symbolType = SymbolType.VOIDFUNC;
        }else{
            symbolType = SymbolType.INTFUNC;
        }
        ArrayList<ValSymbol> params = null;
        if(funcFParams!=null){
            params = funcFParams.check();
        }

        FuncSymbol funcSymbol = new FuncSymbol(name, symbolType, line, params);

        curFuncSymbol = funcSymbol;
        SymbolTableManager.addSymbol(funcSymbol);

        //这里再次把参数加到下一个作用域，上面虽然已经处理过，但是Symbol本身没有depth属性
        SymbolTableManager.createSonTable();
        if(params!=null){
            for(ValSymbol param : params){
                SymbolTableManager.addSymbol(param);
            }
        }
        //有返回值的函数缺少return语句（g错误）
        if(symbolType == SymbolType.INTFUNC){
            missReturn();
        }
        this.block.check();
        SymbolTableManager.gotoFatherTable();
    }

    //有返回值的函数缺少return语句（g错误）
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