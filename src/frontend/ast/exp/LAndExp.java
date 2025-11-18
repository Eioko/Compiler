package frontend.ast.exp;

import frontend.lexer.Token;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

import java.util.ArrayList;

/**
 * LAndExp -> EqExp | LAndExp '&&' EqExp
 */
public class LAndExp extends ComptueExp {
    private EqExp firstEq;
    private ArrayList<Token> andTokens; // '&&'
    private ArrayList<EqExp> otherEqs;

    public LAndExp(EqExp firstEq,
                   ArrayList<Token> andTokens,
                   ArrayList<EqExp> otherEqs) {
        this.firstEq = firstEq;
        this.andTokens = andTokens;
        this.otherEqs = otherEqs;
    }

    public EqExp getFirstEq() { return firstEq; }
    public ArrayList<Token> getAndTokens() { return andTokens; }
    public ArrayList<EqExp> getOtherEqs() { return otherEqs; }

    public void check(){
        firstEq.check();
        for(EqExp e: otherEqs){
            e.check();
        }
    }
    public void buildIr(BasicBlock trueBlock, BasicBlock falseBlock){
        BasicBlock nextBlock = null;
        if(!otherEqs.isEmpty()){
            nextBlock = irBuilder.buildBasicBlock(curfunc);
            firstEq.buildIr(nextBlock, falseBlock);
        }else{
            // 整个就一个eqExp
            firstEq.buildIr(trueBlock, falseBlock);
            return;
        }
        curBlock = nextBlock;
        for (int i = 0; i < otherEqs.size(); i++) {
            EqExp eqExp = otherEqs.get(i);
            if(i!= otherEqs.size()-1){
                BasicBlock newNextBlock = irBuilder.buildBasicBlock(curfunc);
                eqExp.buildIr(newNextBlock, falseBlock);
                curBlock = newNextBlock;
            }else{
                eqExp.buildIr(trueBlock, falseBlock);
            }
        }
    }
}