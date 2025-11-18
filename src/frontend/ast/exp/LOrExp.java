package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.constant.ConstInt;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

import java.util.ArrayList;

/**
 * LOrExp -> LAndExp | LOrExp '||' LAndExp
 */
public class LOrExp extends ComptueExp {
    private LAndExp firstAnd;
    private ArrayList<Token> orTokens; // '||'
    private ArrayList<LAndExp> otherAnds;

    public LOrExp(LAndExp firstAnd,
                  ArrayList<Token> orTokens,
                  ArrayList<LAndExp> otherAnds) {
        this.firstAnd = firstAnd;
        this.orTokens = orTokens;
        this.otherAnds = otherAnds;
    }

    public LAndExp getFirstAnd() { return firstAnd; }
    public ArrayList<Token> getOrTokens() { return orTokens; }
    public ArrayList<LAndExp> getOtherAnds() { return otherAnds; }
    public void check() {
        firstAnd.check();
        for(LAndExp otherAnd : otherAnds) {
            otherAnd.check();
        }
    }
    public void buildIr(BasicBlock trueBlock, BasicBlock falseBlock) {
        BasicBlock nextBlock = null;
        if(!otherAnds.isEmpty()){
            nextBlock = irBuilder.buildBasicBlock(curfunc);
            firstAnd.buildIr(trueBlock, nextBlock);
        }else{
            // 整个就一个andExp
            firstAnd.buildIr(trueBlock, falseBlock);
            return;
        }
        curBlock = nextBlock;
        for (int i = 0; i < otherAnds.size(); i++) {
            LAndExp andExp = otherAnds.get(i);
            if(i!= otherAnds.size()-1){
                BasicBlock newNextBlock = irBuilder.buildBasicBlock(curfunc);
                andExp.buildIr(trueBlock, newNextBlock);
                curBlock = newNextBlock;
            }else{
                andExp.buildIr(trueBlock, falseBlock);
            }
        }
    }
}