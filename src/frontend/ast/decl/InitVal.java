package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.Exp;
import frontend.lexer.Token;

import java.util.ArrayList;
/*
 InitVal → Exp |
            '{' [ Exp { ',' Exp } ] '}'
 */
public class InitVal extends Node {
    private Exp exp0;

    private Token lbrace;
    private Exp exp1;
    private ArrayList<Token> commaTokens;
    private ArrayList<Exp> exps;
    private Token rbrace;

    private int utype;

    public InitVal(Exp exp){
        this.exp0 = exp;
        utype = 0;
    }
    public InitVal(Token lbrace,
                   Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<Exp> exps,
                   Token rbrace){
        this.lbrace = lbrace;
        this.exp0 = exp;
        this.commaTokens = commaTokens;
        this.exp1 = exp;
        this.rbrace = rbrace;
        utype = 1;
    }

}
