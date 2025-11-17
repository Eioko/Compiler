package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.decl.BType;
import frontend.lexer.Token;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

/**
 * FuncFParam -> BType Ident ['[' ']']
 */
public class FuncFParam extends Node {
    private BType bType;
    private Token identToken;   // 标识符
    private Token lbrackToken;  // '['  可为 null
    private Token rbrackToken;  // ']'  可为 null

    public FuncFParam(BType bType,
                      Token identToken,
                      Token lbrackToken,
                      Token rbrackToken) {
        this.bType = bType;
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.rbrackToken = rbrackToken;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public boolean isArrayParam() {
        return lbrackToken != null && rbrackToken != null;
    }

    public Token getLbrackToken() {
        return lbrackToken;
    }

    public Token getRbrackToken() {
        return rbrackToken;
    }
    public ValSymbol check(){
        SymbolType symbolType;
        if(lbrackToken!=null){
            symbolType = SymbolType.INTARRAY;
        }else{
            symbolType = SymbolType.INT;
        }
        return new ValSymbol(identToken.getTokenContent(), symbolType, identToken.getLineNum());
    }
}