package frontend.ast.exp;

import error.ErrorType;
import error.SysyError;
import frontend.lexer.Token;
import midend.symbol.Symbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;

import static error.ErrorManager.addError;
import static error.ErrorManager.errors;

/**
 * LVal -> Ident ['[' Exp ']']
 */
public class LVal extends ComptueExp{
    private Token identToken;
    private Token lbrackToken; // 可为 null
    private Exp indexExp;      // 可为 null
    private Token rbrackToken; // 可为 null

    public LVal(Token identToken,
                Token lbrackToken,
                Exp indexExp,
                Token rbrackToken) {
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.indexExp = indexExp;
        this.rbrackToken = rbrackToken;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public boolean hasIndex() {
        return indexExp != null;
    }

    public Token getLbrackToken() {
        return lbrackToken;
    }

    public Exp getIndexExp() {
        return indexExp;
    }

    public Token getRbrackToken() {
        return rbrackToken;
    }
    public void check(){
        String name = identToken.getTokenContent();
        int line = identToken.getLineNum();
        Symbol symbol = SymbolTableManager.getSymbol(name, line);
        if(symbol == null){
            addError(new SysyError(ErrorType.UNDEFINED_IDENTIFIER, line));
            //这里要return吗？？？
        }
        if(indexExp != null){
            indexExp.check();
        }
    }
    public SymbolType getType(){
        String name = identToken.getTokenContent();
        int line = identToken.getLineNum();
        Symbol symbol = SymbolTableManager.getSymbol(name, line);
        if(symbol != null){
            SymbolType type = symbol.getSymbolType();
            if(type == SymbolType.INT || type == SymbolType.CONSTINT || type == SymbolType.STATICINT){
                return SymbolType.INT;
            }else{
                if(lbrackToken != null){
                    return SymbolType.INT;
                }
                return SymbolType.INTARRAY;
            }
        }else{
            return null;
        }
    }
}