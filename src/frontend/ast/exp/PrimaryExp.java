package frontend.ast.exp;

import frontend.lexer.Token;
import midend.symbol.SymbolType;

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

    public void check() {
        if(utype == 0) {
            exp.check();
        }else if(utype == 1) {
            lVal.check();
        }else{
            number.check();
        }
    }
    public SymbolType getType() {
        //这里会不会出现(a),a为数组
        if(utype == 0) {
            return SymbolType.INT;
        }else if(utype == 1) {
            return lVal.getType();
        }else{
            return SymbolType.INT;
        }
    }
}