package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.exp.Exp;
import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * FuncRParams -> Exp { ',' Exp }
 */
public class FuncRParams extends Node {
    private Exp firstExp;
    private ArrayList<Token> commaTokens;
    private ArrayList<Exp> otherExps;

    public FuncRParams(Exp firstExp,
                       ArrayList<Token> commaTokens,
                       ArrayList<Exp> otherExps) {
        this.firstExp = firstExp;
        this.commaTokens = commaTokens;
        this.otherExps = otherExps;
    }

    public Exp getFirstExp() {
        return firstExp;
    }

    public ArrayList<Token> getCommaTokens() {
        return commaTokens;
    }

    public ArrayList<Exp> getOtherExps() {
        return otherExps;
    }

    public int size() {
        return 1 + (otherExps == null ? 0 : otherExps.size());
    }
    public void check(){
        firstExp.check();
        for(Exp exp : otherExps){
            exp.check();
        }
    }
    public ArrayList<Exp> allArgs(){
        ArrayList<Exp> allArgs = new ArrayList<>();
        allArgs.add(firstExp);
        allArgs.addAll(otherExps);
        return allArgs;
    }
}