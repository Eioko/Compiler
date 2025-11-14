package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;
import midend.ir.IrBuilder;
import midend.ir.IrModule;
import midend.ir.constant.ConstArray;
import midend.ir.constant.ConstInt;
import midend.ir.constant.Constant;
import midend.ir.instruction.Alloca;
import midend.ir.instruction.GEP;
import midend.ir.type.ArrayType;
import midend.ir.value.GlobalVariable;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

import java.lang.reflect.Array;

/*
 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
 */
public class ConstDef extends Node {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp;
    private Token rbrack = null;
    private Token assignToken;
    private ConstInitVal constInitVal;

    private int size;
    public ConstDef(Token ident,
                    Token lbrack,
                    ConstExp constExp,
                    Token rbrack,
                    Token assignToken,
                    ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assignToken = assignToken;
        this.constInitVal = constInitVal;
    }

    public void check(){
        String name = ident.getTokenContent();
        int line = ident.getLineNum();
        SymbolType symbolType;
        if(lbrack != null){
            symbolType = SymbolType.CONSTINTARRAY;
        }else{
            symbolType = SymbolType.CONSTINT;
        }
        //这里先冷处理ConstExp，都先不算数值，因此不用size那个构造函数
        ValSymbol valSymbol = new ValSymbol(name, symbolType, line);
        SymbolTableManager.addSymbol(valSymbol);
        if(constExp != null){
            constExp.check();
        }
        constInitVal.check();
    }
    public void buildIr(){
        ValSymbol valSymbol = (ValSymbol) SymbolTableManager.getSymbol(ident.getTokenContent());
        if(lbrack == null){
            // 单变量
            constInitVal.buildIr();
            if(SymbolTableManager.isGlobal()){
                //全局常量分配
                GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(), (Constant)valueUp,true);
                valSymbol.setIrValue(globalVariable);
            }else{
                // 局部常量分配
                Alloca alloc = irBuilder.buildConstAlloca(valueUp.getValueType(), curBlock, (Constant) valueUp);
                irBuilder.buildStore(curBlock, valueUp, alloc);
            }
        }else{
            // 数组
            constExp.buildIr();
            //这里文法保证 “各维长度的 ConstExp 都必须能在编译时求值到非负整数”
            size = ((ConstInt)valueUp).getNumber();
            constInitVal.buildIr();
            if(SymbolTableManager.isGlobal()){
                GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(), (Constant)valueUp,true);
                valSymbol.setIrValue(globalVariable);
            }else{
                ArrayType arrayType = new ArrayType(size);
                Alloca allocArray = irBuilder.buildConstAlloca(arrayType, curBlock, (ConstArray) valueUp);
                GEP basePtr = irBuilder.buildGEP(curBlock, allocArray, ConstInt.ZERO, ConstInt.ZERO);
                // 利用 store 往内存中存值
                for (int i = 0; i < valueArrayUp.size(); i++) {
                    if (i == 0) {
                        irBuilder.buildStore(curBlock, valueArrayUp.get(i), basePtr);
                    } else {
                        // 这里利用的是一维的 GEP，此时的返回值依然是 int*
                        GEP curPtr = irBuilder.buildGEP(curBlock, basePtr, new ConstInt(i));
                        irBuilder.buildStore(curBlock, valueArrayUp.get(i), curPtr);
                    }
                }
            }
        }
    }
}
