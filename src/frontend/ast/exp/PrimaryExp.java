package frontend.ast.exp;

import frontend.lexer.Token;

/**
 * PrimaryExp -> '(' Exp ')' | LVal | Number
 */

public class PrimaryExp extends ComptueExp {
    private Token lparenToken;
    private Exp exp;
    private Token rparenToken;

    private LVal lVal;
    private Number number;

    private int utype;

    private PrimaryExp() {}

    public PrimaryExp(Token lp, Exp exp, Token rp) {
        this.lparenToken = lp;
        this.exp = exp;
        this.rparenToken = rp;
        utype = 0;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
        utype = 1;
    }

    public PrimaryExp(Number number) {
        this.number = number;
        utype = 2;
    }

    public boolean isParen() { return exp != null; }
    public boolean isLVal() { return lVal != null; }
    public boolean isNumber() { return number != null; }

    public Exp getExp() { return exp; }
    public LVal getLVal() { return lVal; }
    public Number getNumber() { return number; }
    public Token getLparenToken() { return lparenToken; }
    public Token getRparenToken() { return rparenToken; }
}