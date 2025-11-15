package frontend.ast.exp;

/**
 * Cond -> LOrExp
 */
public class Cond extends ComptueExp {
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }
    public void check(){
        lOrExp.check();
    }
    public void buildIr(){
        lOrExp.buildIr();
    }
}