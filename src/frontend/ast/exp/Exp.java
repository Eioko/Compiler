package frontend.ast.exp;

import frontend.ast.Node;
import midend.symbol.SymbolType;

/**
 * Exp -> AddExp
 */
public class Exp extends Node {
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }
    public void check(){
        addExp.check();
    }
    public SymbolType getType() {
        return addExp.getType();
    }
    public void buildIr(){
        addExp.buildIr();
    }
}