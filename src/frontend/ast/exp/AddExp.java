package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.constant.ConstInt;
import midend.ir.value.Value;
import midend.symbol.SymbolType;

import java.util.ArrayList;

/**
 * AddExp -> MulExp | AddExp ('+' | '-') MulExp
 */
public class AddExp extends ComptueExp {
    private MulExp firstMul;
    private ArrayList<Token> opTokens;   // '+', '-'
    private ArrayList<MulExp> otherMuls;

    public AddExp(MulExp firstMul,
                  ArrayList<Token> opTokens,
                  ArrayList<MulExp> otherMuls) {
        this.firstMul = firstMul;
        this.opTokens = opTokens;
        this.otherMuls = otherMuls;
    }

    public MulExp getFirstMul() { return firstMul; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<MulExp> getOtherMuls() { return otherMuls; }
    public int size() { return 1 + (otherMuls == null ? 0 : otherMuls.size()); }

    public void check(){
        firstMul.check();
        for(MulExp mulExp : otherMuls){
            mulExp.check();
        }
    }
    public SymbolType getType() {
        return firstMul.getType();
    }
    public ArrayList<MulExp> getMulExps(){
        ArrayList<MulExp> mulExps = new ArrayList<>();
        mulExps.add(firstMul);
        mulExps.addAll(otherMuls);
        return mulExps;
    }

    public void buildIr(){
        ArrayList<MulExp> mulExps = getMulExps();
        if(global){
            int sum = 0;
            firstMul.buildIr();
            sum += valueIntUp;
            for(int i=1; i < mulExps.size(); i++){
                mulExps.get(i).buildIr();
                Token opToken = opTokens.get(i - 1);
                if(opToken.getTokenContent().equals("+")){
                    sum += valueIntUp;
                }else{
                    sum -= valueIntUp;
                }
            }
            valueIntUp = sum;
            valueUp = new ConstInt(valueIntUp);
        }else{
            firstMul.buildIr();
            Value sum = valueUp;
            for(int i = 1; i < mulExps.size(); i++){
                MulExp mulExp = mulExps.get(i);
                Token opToken = opTokens.get(i - 1);
                mulExp.buildIr();
                if(opToken.getTokenContent().equals("+")){
                    sum = irBuilder.buildAdd(curBlock, sum, valueUp);
                }else{
                    sum = irBuilder.buildSub(curBlock, sum, valueUp);
                }
            }
            valueUp = sum;
        }
    }
}