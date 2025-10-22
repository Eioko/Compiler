package frontend.ast.exp;

import error.ErrorType;
import error.SysyError;
import frontend.ast.func.FuncRParams;
import frontend.lexer.Token;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

import java.util.ArrayList;

import static error.ErrorManager.addError;

/**
 * UnaryExp -> PrimaryExp
 *          | Ident '(' [FuncRParams] ')'
 *          | UnaryOp UnaryExp
 */
public class UnaryExp extends ComptueExp {

    private int utype;

    // PRIMARY
    private PrimaryExp primaryExp;

    // CALL
    private Token identToken;
    private Token lparenToken;
    private FuncRParams funcRParams; // 可为 null
    private Token rparenToken;

    // PREFIX
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;  // 递归


    public UnaryExp(PrimaryExp p) {
        this.primaryExp = p;
        utype = 0;
    }

    public UnaryExp (Token ident, Token lp, FuncRParams params, Token rp) {
        this.identToken = ident;
        this.lparenToken = lp;
        this.funcRParams = params;
        this.rparenToken = rp;
        utype = 1;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp inner) {
        this.unaryOp = unaryOp;
        this.unaryExp = inner;
        utype = 2;
    }

    public void check(){
        if(utype == 0){
            primaryExp.check();
        }else if(utype == 1){
            String name = identToken.getTokenContent();
            int line = identToken.getLineNum();
            FuncSymbol funcSymbol = (FuncSymbol) SymbolTableManager.getSymbol(name, line);
            if(funcSymbol == null){
                if(!name.equals("getint")){
                    addError(new SysyError(ErrorType.UNDEFINED_IDENTIFIER, line));
                }
                //这里要return吗？？？-----要！
                return;
            }
            if(funcRParams==null){
                return;
            }
            int formatNum = funcSymbol.getParams().size();
            int realNum = funcRParams.getOtherExps().size()+1;

            ArrayList<ValSymbol> formatArgs = funcSymbol.getParams();
            ArrayList<Exp> realArgs = funcRParams.allArgs();
            if(realNum != formatNum){
                addError(new SysyError(ErrorType.ARGUMENT_COUNT_MISMATCH, line));
            }else{
                for(int i=0;i<formatNum;i++){
                    SymbolType symbolType = realArgs.get(i).getType();
                    if(symbolType == SymbolType.INTFUNC ||
                            symbolType == SymbolType.CONSTINT || symbolType == SymbolType.STATICINT){
                        symbolType = SymbolType.INT;
                    }
                    if(symbolType == SymbolType.STATICINTARRAY || symbolType == SymbolType.CONSTINTARRAY){
                        symbolType = SymbolType.INTARRAY;
                    }
                    if(formatArgs.get(i).getSymbolType()!=symbolType){
                        addError(new SysyError(ErrorType.ARGUMENT_TYPE_MISMATCH, line));
                    }
                }
            }

            funcRParams.check();
        }else if(utype == 2){
            unaryOp.check();
            unaryExp.check();
        }
    }
    public SymbolType getType() {
        if(utype == 0){
            return primaryExp.getType();
        }else if(utype == 1){
            String name = identToken.getTokenContent();
            int line = identToken.getLineNum();
            FuncSymbol funcSymbol = (FuncSymbol) SymbolTableManager.getSymbol(name, line);
            if(funcSymbol != null){
                return funcSymbol.getSymbolType();
            }else{
                return null;
            }
        }else{
            return SymbolType.INT;
        }
    }

    public PrimaryExp getPrimaryExp() { return primaryExp; }

    public Token getIdentToken() { return identToken; }
    public Token getLparenToken() { return lparenToken; }
    public FuncRParams getFuncRParams() { return funcRParams; }
    public Token getRparenToken() { return rparenToken; }

    public UnaryOp getUnaryOpToken() { return unaryOp; }
    public UnaryExp getUnaryExp() { return unaryExp; }
}