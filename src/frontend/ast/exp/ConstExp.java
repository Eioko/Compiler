package frontend.ast.exp;

/**
 * ConstExp -> AddExp
 * 语义检查时验证内部使用的标识符为常量。
 */
public class ConstExp extends ComptueExp {
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }
    public void check(){
        addExp.check();
    }
    public void buildIr(){
        global = true;
        addExp.buildIr();
        global = false;
    }
}