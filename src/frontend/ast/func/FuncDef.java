package frontend.ast.func;

import error.SysyError;
import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.ast.block.BlockItem;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import midend.ir.type.*;
import midend.ir.value.BasicBlock;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

import java.util.ArrayList;


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
            block.missReturn();
        }
        this.block.check();
        SymbolTableManager.gotoFatherTable();
    }
    public void buildIr(){
        FuncSymbol funcSymbol = (FuncSymbol) SymbolTableManager.getSymbol(identToken.getTokenContent());
        DataType returnType;
        if(funcSymbol.getSymbolType() == SymbolType.VOIDFUNC) {
            returnType = new VoidType();
        }else{
            returnType = new IntegerType();
        }
        ArrayList<DataType> paramTypes = new ArrayList<>();
        for(ValSymbol paramSymbol : funcSymbol.getParams()){
            //已经在check里面处理过，形参是Int或者IntArray
            if(paramSymbol.getSymbolType() == SymbolType.INT){
                paramTypes.add(new IntegerType());
            }else if(paramSymbol.getSymbolType() == SymbolType.INTARRAY){
                paramTypes.add(new PointerType(new IntegerType()));
            }
        }
        curfunc = irBuilder.buildFunction(funcSymbol.getSymbolName(), returnType, paramTypes);
        funcSymbol.setIrValue(curfunc);
        //创建函数体的基本块
        BasicBlock entryBlock = irBuilder.buildBasicBlock(curfunc);
        curBlock = entryBlock;
        SymbolTableManager.gotoNextSonTable();
        if(funcFParams!=null){
            funcFParams.buildIr();
        }
        block.buildIr();
        SymbolTableManager.gotoFatherTable();
    }

}