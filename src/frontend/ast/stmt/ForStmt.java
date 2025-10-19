package frontend.ast.stmt;

import frontend.ast.Node;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.lexer.Token;

import java.util.ArrayList;

public class ForStmt extends Node {
    private LVal lVal;
    private Token assignToken;
    private Exp exp;
    private ArrayList<Token> commaTokens;
    private ArrayList<LVal> lvals;
    private ArrayList<Token> assignmentTokens;
    private ArrayList<Exp> exps;

    public ForStmt(LVal lVal, Token assignToken, Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<LVal> lvals,
                   ArrayList<Token> assignmentTokens,
                   ArrayList<Exp> exps
                   ) {
        this.lVal = lVal;
        this.assignToken = assignToken;
        this.exp = exp;
        this.commaTokens = commaTokens;
        this.lvals = lvals;
        this.assignmentTokens = assignmentTokens;
        this.exps = exps;
    }
    public void check(){

    }
}
