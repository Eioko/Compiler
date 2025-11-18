package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.instruction.Icmp;
import midend.ir.value.Value;

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
    public void buildIr(){
        firstAdd.buildIr();
        Value res = valueUp;

        for (int i = 0; i < otherAdds.size(); i++) {
            AddExp addExp = otherAdds.get(i);
            addExp.buildIr();
            Token opToken = opTokens.get(i);
            Value right = valueUp;
            if(opToken.getTokenContent().equals("<")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.LT, res, right);
            }else if(opToken.getTokenContent().equals(">")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.GT, res, right);
            }else if(opToken.getTokenContent().equals("<=")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.LE, res, right);
            }else if(opToken.getTokenContent().equals(">=")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.GE, res, right);
            }
        }
        valueUp = res;
    }
}