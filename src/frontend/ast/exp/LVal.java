package frontend.ast.exp;

import error.ErrorType;
import error.SysyError;
import frontend.lexer.Token;
import midend.ir.constant.ConstArray;
import midend.ir.constant.ConstInt;
import midend.ir.constant.Constant;
import midend.ir.instruction.Alloca;
import midend.ir.instruction.GEP;
import midend.ir.instruction.Load;
import midend.ir.type.ArrayType;
import midend.ir.type.IntegerType;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.GlobalVariable;
import midend.ir.value.Value;
import midend.symbol.*;

import static error.ErrorManager.addError;

/**
 * LVal -> Ident ['[' Exp ']']
 */
public class LVal extends ComptueExp{
    private Token identToken;
    private Token lbrackToken; // 可为 null
    private Exp indexExp;      // 可为 null
    private Token rbrackToken; // 可为 null

    public LVal(Token identToken,
                Token lbrackToken,
                Exp indexExp,
                Token rbrackToken) {
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.indexExp = indexExp;
        this.rbrackToken = rbrackToken;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public boolean hasIndex() {
        return indexExp != null;
    }

    public Token getLbrackToken() {
        return lbrackToken;
    }

    public Exp getIndexExp() {
        return indexExp;
    }

    public Token getRbrackToken() {
        return rbrackToken;
    }
    public void check(){
        String name = identToken.getTokenContent();
        int line = identToken.getLineNum();
        Symbol symbol = SymbolTableManager.getSymbol(name);
        if(symbol == null){
            addError(new SysyError(ErrorType.UNDEFINED_IDENTIFIER, line));
            //这里要return吗？？？
        }
        if(indexExp != null){
            indexExp.check();
        }
    }
    public SymbolType getType(){
        String name = identToken.getTokenContent();
        int line = identToken.getLineNum();
        Symbol symbol = SymbolTableManager.getSymbol(name);
        if(symbol != null){
            SymbolType type = symbol.getSymbolType();
            if(type == SymbolType.INT || type == SymbolType.CONSTINT || type == SymbolType.STATICINT){
                return SymbolType.INT;
            }else{
                if(lbrackToken != null){
                    return SymbolType.INT;
                }
                return SymbolType.INTARRAY;
            }
        }else{
            return null;
        }
    }
    public void buildIr(){
        String name = identToken.getTokenContent();
        Symbol symbol = SymbolTableManager.getSymbol(name);
        Value value = symbol.getIrValue();
        if(value == null){
            symbol = (ValSymbol) SymbolTableManager.GetSymbolFromFather(identToken.getTokenContent());
            value = symbol.getIrValue();
        }

        if(value.getValueType() instanceof IntegerType){
            valueUp = value;
        }else{
            // 是PointerType, 取其指向的内容
            ValueType valueType = ((PointerType)value.getValueType()).getPointeeType();

            if(valueType instanceof IntegerType){
                //局部或者全局变量
                if(global && value instanceof GlobalVariable){
                    ConstInt init = (ConstInt) ((GlobalVariable)value).getInitValue();
                    valueIntUp = init.getNumber();
                    valueUp = new ConstInt(valueIntUp);
                }else{
                    valueUp = value;
                }
            }else if(valueType instanceof PointerType){
                // 数组形参 a[]，这时value其实是一个指向形参a[]的局部变量，即int**
                // 取一维地址，指向int
                Value ptrValue = irBuilder.buildLoad(curBlock, value);

                if(indexExp == null){
                    valueUp = ptrValue;
                }else{
                    indexExp.buildIr();
                    Value indexValue = valueUp;
                    GEP gep = irBuilder.buildGEP(curBlock, ptrValue, indexValue);
                    valueUp = gep;
                }
            }else if(valueType instanceof ArrayType){
                // 全局/局部数组变量
                if(global && value instanceof GlobalVariable){
                    //全局数组变量，直接取初值
                    Constant arrayInit = ((GlobalVariable)value).getInitValue();

                    indexExp.buildIr();
                    int index = valueIntUp;
                    valueUp = ((ConstArray)arrayInit).getElementAt(index);
                    valueIntUp = ((ConstInt)valueUp).getNumber();
                }else if (global && value instanceof Alloca) {
                    //局部常量数组变量，直接取初值
                    ConstArray initVal = ((Alloca) value).getInitVal();
                    indexExp.buildIr();
                    int index = valueIntUp;
                    valueUp = initVal.getElementAt(index);
                    valueIntUp = ((ConstInt)valueUp).getNumber();
                }else{
                    //局部数组变量
                    GEP basePtr = irBuilder.buildGEP(curBlock, value, ConstInt.ZERO, ConstInt.ZERO);
                    if(indexExp == null){
                        //取地址
                        valueUp = basePtr;
                    }else{
                        indexExp.buildIr();
                        Value indexValue = valueUp;
                        GEP gep = irBuilder.buildGEP(curBlock, basePtr, indexValue);
                        valueUp = gep;
                    }
                }
            }
        }
    }
}