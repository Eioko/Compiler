package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.value.Value;

import java.util.ArrayList;

/**
 * LAndExp -> EqExp | LAndExp '&&' EqExp
 */
public class LAndExp extends ComptueExp {
    private EqExp firstEq;
    private ArrayList<Token> andTokens; // '&&'
    private ArrayList<EqExp> otherEqs;

    public LAndExp(EqExp firstEq,
                   ArrayList<Token> andTokens,
                   ArrayList<EqExp> otherEqs) {
        this.firstEq = firstEq;
        this.andTokens = andTokens;
        this.otherEqs = otherEqs;
    }

    public EqExp getFirstEq() { return firstEq; }
    public ArrayList<Token> getAndTokens() { return andTokens; }
    public ArrayList<EqExp> getOtherEqs() { return otherEqs; }

    public void check(){
        firstEq.check();
        for(EqExp e: otherEqs){
            e.check();
        }
    }

}