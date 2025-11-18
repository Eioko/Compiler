package frontend.ast.exp;

import midend.ir.value.BasicBlock;

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
    public void buildIr(BasicBlock trueBlock, BasicBlock falseBlock){
        lOrExp.buildIr(trueBlock, falseBlock);
    }
}