package frontend.ast.exp;

import frontend.lexer.Token;
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
}