package frontend.ast.exp;

import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
 */
public class RelExp extends ComptueExp{
    private AddExp firstAdd;
    private ArrayList<Token> opTokens;
    private ArrayList<AddExp> otherAdds;

    public RelExp(AddExp firstAdd,
                  ArrayList<Token> opTokens,
                  ArrayList<AddExp> otherAdds) {
        this.firstAdd = firstAdd;
        this.opTokens = opTokens;
        this.otherAdds = otherAdds;
    }

    public AddExp getFirstAdd() { return firstAdd; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<AddExp> getOtherAdds() { return otherAdds; }

    public void check(){
        firstAdd.check();
        for(AddExp addExp : otherAdds){
            addExp.check();
        }
    }
}