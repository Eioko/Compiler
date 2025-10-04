package frontend.ast.exp;

import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * LOrExp -> LAndExp | LOrExp '||' LAndExp
 */
public class LOrExp extends ComptueExp {
    private LAndExp firstAnd;
    private ArrayList<Token> orTokens; // '||'
    private ArrayList<LAndExp> otherAnds;

    public LOrExp(LAndExp firstAnd,
                  ArrayList<Token> orTokens,
                  ArrayList<LAndExp> otherAnds) {
        this.firstAnd = firstAnd;
        this.orTokens = orTokens;
        this.otherAnds = otherAnds;
    }

    public LAndExp getFirstAnd() { return firstAnd; }
    public ArrayList<Token> getOrTokens() { return orTokens; }
    public ArrayList<LAndExp> getOtherAnds() { return otherAnds; }
}