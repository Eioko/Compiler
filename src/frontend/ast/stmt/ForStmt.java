package frontend.ast.stmt;

import error.ErrorType;
import error.SysyError;
import frontend.ast.Node;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.lexer.Token;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;
import midend.symbol.Symbol;
import midend.symbol.SymbolTableManager;

import java.util.ArrayList;

import static error.ErrorManager.addError;

/*
ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
 */
public class ForStmt extends Node {
    private LVal lVal;
    private Token assignToken;
    private Exp exp;
    private ArrayList<Token> commaTokens;
    private ArrayList<LVal> lvals;
    private ArrayList<Token> assignmentTokens;
    private ArrayList<Exp> exps;

    public ForStmt(LVal lVal, Token assignToken, Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<LVal> lvals,
                   ArrayList<Token> assignmentTokens,
                   ArrayList<Exp> exps
                   ) {
        this.lVal = lVal;
        this.assignToken = assignToken;
        this.exp = exp;
        this.commaTokens = commaTokens;
        this.lvals = lvals;
        this.assignmentTokens = assignmentTokens;
        this.exps = exps;
    }
    public void check(){
        assignToConst(lVal);
        for(LVal l : lvals){
            assignToConst(l);
        }
        lVal.check();
        exp.check();
        for(int i=0;i<assignmentTokens.size();i++){
            lvals.get(i).check();
            exps.get(i).check();
        }
    }
    public void assignToConst(LVal lVal) {
        String name = lVal.getIdentToken().getTokenContent();
        int line = lVal.getIdentToken().getLineNum();
        Symbol symbol = SymbolTableManager.getSymbol(name);

        if(symbol != null){
            //这里不处理未定义错误，防止重复记录一个错误
            if(symbol.isConst()){
                addError(new SysyError(ErrorType.ASSIGN_TO_CONST, line));
            }
        }
    }
    public void buildIr(){
        lVal.buildIr();
        Value addr = valueUp;
        exp.buildIr();
        Value val = valueUp;
        irBuilder.buildStore(curBlock, val, addr);

        for(int i=0;i<lvals.size();i++){
            lvals.get(i).buildIr();
            Value laddr = valueUp;
            exps.get(i).buildIr();
            Value rval = valueUp;
            irBuilder.buildStore(curBlock, rval, laddr);
        }
    }
}
