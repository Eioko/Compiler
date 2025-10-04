package frontend.ast.exp;

import frontend.ast.func.FuncRParams;
import frontend.lexer.Token;

/**
 * UnaryExp -> PrimaryExp
 *          | Ident '(' [FuncRParams] ')'
 *          | UnaryOp UnaryExp
 */
public class UnaryExp extends ComptueExp {

    private int utype;

    // PRIMARY
    private PrimaryExp primaryExp;

    // CALL
    private Token identToken;
    private Token lparenToken;
    private FuncRParams funcRParams; // 可为 null
    private Token rparenToken;

    // PREFIX
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;  // 递归


    public UnaryExp(PrimaryExp p) {
        this.primaryExp = p;
        utype = 0;
    }

    public UnaryExp (Token ident, Token lp, FuncRParams params, Token rp) {
        this.identToken = ident;
        this.lparenToken = lp;
        this.funcRParams = params;
        this.rparenToken = rp;
        utype = 1;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp inner) {
        this.unaryOp = unaryOp;
        this.unaryExp = inner;
    }


    public PrimaryExp getPrimaryExp() { return primaryExp; }

    public Token getIdentToken() { return identToken; }
    public Token getLparenToken() { return lparenToken; }
    public FuncRParams getFuncRParams() { return funcRParams; }
    public Token getRparenToken() { return rparenToken; }

    public UnaryOp getUnaryOpToken() { return unaryOp; }
    public UnaryExp getUnaryExp() { return unaryExp; }
}