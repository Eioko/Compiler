package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.constant.ConstInt;
import midend.ir.value.Value;
import midend.symbol.SymbolType;

import java.util.ArrayList;

/**
 * MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
 */
public class MulExp extends ComptueExp {
    private UnaryExp firstUnary;
    private ArrayList<Token> opTokens;     // '*', '/', '%'
    private ArrayList<UnaryExp> otherUnaries;

    public MulExp(UnaryExp firstUnary,
                  ArrayList<Token> opTokens,
                  ArrayList<UnaryExp> otherUnaries) {
        this.firstUnary = firstUnary;
        this.opTokens = opTokens;
        this.otherUnaries = otherUnaries;
    }

    public UnaryExp getFirstUnary() { return firstUnary; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<UnaryExp> getOtherUnaries() { return otherUnaries; }

    public int size() { return 1 + (otherUnaries == null ? 0 : otherUnaries.size()); }

    public void check(){
        firstUnary.check();
        for (UnaryExp unaryExp : otherUnaries) {
            unaryExp.check();
        }
    }
    public SymbolType getType() {
        return firstUnary.getType();
    }
    public void buildIr(){
        if(global){
            int sum = 0;
            firstUnary.buildIr();
            sum += valueIntUp;
            for(int i=0; i < size(); i++){
                otherUnaries.get(i).buildIr();
                Token opToken = opTokens.get(i);
                if(opToken.getTokenContent().equals("*")){
                    sum = sum * valueIntUp;
                }else if(opToken.getTokenContent().equals("/")){
                    sum = sum / valueIntUp;
                }else{
                    sum = sum % valueIntUp;
                }
            }
            valueIntUp = sum;
            valueUp = new ConstInt(valueIntUp);
        }else{
            firstUnary.buildIr();
            Value sum = valueUp;
            for(int i=0; i < size(); i++) {
                otherUnaries.get(i).buildIr();
                Token opToken = opTokens.get(i);
                if (opToken.getTokenContent().equals("*")) {
                    sum = irBuilder.buildMul(curBlock, sum, valueUp);
                } else if (opToken.getTokenContent().equals("/")) {
                    sum = irBuilder.buildDiv(curBlock, sum, valueUp);
                } else {
                    sum = irBuilder.buildMod(curBlock, sum, valueUp);
                }
            }
            valueUp = sum;
        }
    }
}