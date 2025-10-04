package frontend.ast.exp;

import frontend.ast.Node;

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
}