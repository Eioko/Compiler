package frontend.ast.exp;

import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * EqExp -> RelExp | EqExp ('==' | '!=') RelExp
 */
public class EqExp extends ComptueExp {
    private RelExp firstRel;
    private ArrayList<Token> opTokens;
    private ArrayList<RelExp> otherRels;

    public EqExp(RelExp firstRel,
                 ArrayList<Token> opTokens,
                 ArrayList<RelExp> otherRels) {
        this.firstRel = firstRel;
        this.opTokens = opTokens;
        this.otherRels = otherRels;
    }

    public RelExp getFirstRel() { return firstRel; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<RelExp> getOtherRels() { return otherRels; }
}