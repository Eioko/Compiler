package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.constant.ConstInt;
import midend.ir.instruction.Icmp;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

import java.util.ArrayList;

/**
 * EqExp -> RelExp | EqExp ('==' | '!=') RelExp
 */
public class EqExp extends ComptueExp {
    private RelExp firstRel;
    private ArrayList<Token> opTokens;
    private ArrayList<RelExp> otherRels;

    public EqExp(RelExp firstRel,
                 ArrayList<Token> opTokens,
                 ArrayList<RelExp> otherRels) {
        this.firstRel = firstRel;
        this.opTokens = opTokens;
        this.otherRels = otherRels;
    }

    public RelExp getFirstRel() { return firstRel; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<RelExp> getOtherRels() { return otherRels; }
    public void check(){
        firstRel.check();
        for(RelExp otherRel: otherRels){
            otherRel.check();
        }
    }
    public void buildIr(BasicBlock trueBlock, BasicBlock falseBlock){
        firstRel.buildIr();
        Value res = valueUp;
        for (int i = 0; i < otherRels.size(); i++) {
            RelExp relExp = otherRels.get(i);
            relExp.buildIr();
            Token opToken = opTokens.get(i);
            Value right = valueUp;
            if(opToken.getTokenContent().equals("==")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.EQ, res, right);
            }else if(opToken.getTokenContent().equals("!=")){
                res = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.NE, res, right);
            }
        }
        Value icmp = res;
        if(otherRels.isEmpty() && firstRel.getOtherAdds().isEmpty()){
            // 只有一个RelExp，没有进行过比较，直接判断是否为0
            icmp = irBuilder.buildIcmp(curBlock, Icmp.IcmpOp.NE, res, new ConstInt(0));
        }
        irBuilder.buildCondBr(curBlock, icmp, trueBlock, falseBlock);
    }
}