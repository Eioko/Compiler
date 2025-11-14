package frontend.ast.func;

import frontend.ast.Node;
import frontend.lexer.Token;
import midend.ir.IrBuilder;
import midend.ir.instruction.Alloca;
import midend.symbol.SymbolTableManager;
import midend.symbol.ValSymbol;

import java.util.ArrayList;

/**
 * FuncFParams -> FuncFParam { ',' FuncFParam }
 * 采用首元素 + 逗号 tokens + 后续元素列表的模式
 */
public class FuncFParams extends Node {
    private FuncFParam firstParam;
    private ArrayList<Token> commaTokens;     // 每个 ',' 对应一个
    private ArrayList<FuncFParam> otherParams; // 与 commaTokens 对齐

    public FuncFParams(FuncFParam firstParam,
                      ArrayList<Token> commaTokens,
                      ArrayList<FuncFParam> otherParams) {
        this.firstParam = firstParam;
        this.commaTokens = commaTokens;
        this.otherParams = otherParams;
    }

    public FuncFParam getFirstParam() {
        return firstParam;
    }

    public ArrayList<Token> getCommaTokens() {
        return commaTokens;
    }

    public ArrayList<FuncFParam> getOtherParams() {
        return otherParams;
    }
    public ArrayList<FuncFParam> getAllParams(){
        ArrayList<FuncFParam> res = new ArrayList<>();
        res.add(firstParam);
        res.addAll(otherParams);
        return res;
    }

    public int size() {
        return 1 + (otherParams == null ? 0 : otherParams.size());
    }
    public ArrayList<ValSymbol> check(){
        ArrayList<ValSymbol> res = new ArrayList<>();
        res.add(firstParam.check());
        for(FuncFParam param : otherParams) {
            res.add(param.check());
        }

        return res;
    }
    public void buildIr(){
        ArrayList<FuncFParam> allParams = getAllParams();
        for (int i =0 ; i<allParams.size(); i++) {
            Alloca alloca = irBuilder.buildAlloca(curfunc.getArguments().get(i).getValueType(), curBlock);
            irBuilder.buildStore(curBlock, curfunc.getArguments().get(i), alloca);
            FuncFParam param = allParams.get(i);
            ValSymbol paramSymbol = (ValSymbol) SymbolTableManager.getSymbol(param.getIdentToken().getTokenContent());
            paramSymbol.setIrValue(alloca);
            //局部的函数参数也是一个指针的Value？？
        }
    }

}