package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;
import midend.ir.constant.ConstArray;
import midend.ir.constant.ConstInt;
import midend.ir.constant.Constant;
import midend.ir.constant.ZeroInitializer;
import midend.ir.instruction.Alloca;
import midend.ir.instruction.GEP;
import midend.ir.type.ArrayType;
import midend.ir.type.IntegerType;
import midend.ir.value.GlobalVariable;
import midend.ir.value.Value;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

import java.util.ArrayList;

/*
     VarDef → Ident [ '[' ConstExp ']' ]
            | Ident [ '[' ConstExp ']' ] '=' InitVal
 */
public class VarDef extends Node {
    private Token ident;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;

    private Token assignToken;
    private InitVal initVal;

    private int utype;

    private int size;
    public VarDef (Token ident,
                   Token lbrack,
                   ConstExp constExp,
                   Token rbrack){
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;

        this.utype = 0;
    }
    public VarDef (Token ident,
                   Token lbrack,
                   ConstExp constExp,
                   Token rbrack,
                   Token assignToken,
                   InitVal initVal){
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assignToken = assignToken;
        this.initVal = initVal;

        this.utype = 1;
    }
    public void check(boolean isStatic){
        String name = ident.getTokenContent();
        SymbolType symbolType;
        int line = ident.getLineNum();
        if(lbrack!=null){
            if(isStatic){
                symbolType = SymbolType.STATICINTARRAY;
            }else{
                symbolType = SymbolType.INTARRAY;
            }
        }else{
            if(isStatic){
                symbolType = SymbolType.STATICINT;
            }else{
                symbolType = SymbolType.INT;
            }
        }
        ValSymbol valSymbol = new ValSymbol(name, symbolType, line);
        SymbolTableManager.addSymbol(valSymbol);
        if(constExp != null){
            constExp.check();
        }
        if(utype==1){
            initVal.check();
        }
    }
    public void buildIr(){
        ValSymbol valSymbol = (ValSymbol) SymbolTableManager.getSymbol(ident.getTokenContent());
        if(lbrack == null){
            // 单变量
            if(SymbolTableManager.isGlobal()){
                // 全局单变量
                if(utype == 0){
                    // 全局变量未初始化，默认0
                    GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(), ConstInt.ZERO , false);
                    valSymbol.setIrValue(globalVariable);
                }else{
                    // 全局变量已初始化
                    global = true;
                    initVal.buildIr();
                    global = false;
                    GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(), (Constant)valueUp , false);
                    valSymbol.setIrValue(globalVariable);
                }
            }else{
                //局部单变量
                Alloca alloc = irBuilder.buildAlloca(new IntegerType(), curBlock);
                valSymbol.setIrValue(alloc);
                if(utype == 0){
                    // 局部变量未初始化
                }else{
                    // 局部变量已初始化
                    initVal.buildIr();
                    irBuilder.buildStore(curBlock, valueUp, alloc);
                }
            }
        }else{
            // 数组
            constExp.buildIr();
            size = ((ConstInt)valueUp).getNumber();
            globalArrayLen = size;

            ArrayType arrayType = new ArrayType(size);
            if(SymbolTableManager.isGlobal()){
                // 全局数组
                if(utype == 0){
                    // 全局数组未初始化，默认0
                    GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(),
                            new ZeroInitializer(arrayType), false);
                    valSymbol.setIrValue(globalVariable);
                }else{
                    // 全局数组已初始化
                    global = true;
                    initVal.buildIr();
                    global = false;

                    ArrayList<Constant> constArray = new ArrayList<>();
                    for (Value value : valueArrayUp)
                    {
                        constArray.add((ConstInt) value);
                    }
                    ConstArray initArray = new ConstArray(constArray, globalArrayLen);

                    GlobalVariable globalVariable = irBuilder.buildGlobalVariable(ident.getTokenContent(),
                            initArray , false);
                    //symbol 存的是一个指向initVal类型的指针，这里constArray，就是ArrayType
                    valSymbol.setIrValue(globalVariable);
                }
            }else{
                // 局部数组
                Alloca alloc = irBuilder.buildAlloca(arrayType, curBlock);

                //symbol 存的是一个ArrayType的指针
                valSymbol.setIrValue(alloc);
                if(utype == 0){
                    // 局部数组未初始化
                }else {
                    // 局部数组已初始化
                    initVal.buildIr();
                    GEP basePtr = irBuilder.buildGEP(curBlock, alloc, ConstInt.ZERO, ConstInt.ZERO);
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
}
