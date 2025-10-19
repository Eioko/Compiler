package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;
import midend.symbol.ValSymbol;

/*
 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
 */
public class ConstDef extends Node {
    private Token ident;
    private Token lbrack = null;
    private ConstExp constExp;
    private Token rbrack = null;
    private Token assignToken;
    private ConstInitVal constInitVal;

    public ConstDef(Token ident,
                    Token lbrack,
                    ConstExp constExp,
                    Token rbrack,
                    Token assignToken,
                    ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assignToken = assignToken;
        this.constInitVal = constInitVal;
    }

    public void check(){
        String name = ident.getTokenContent();
        int line = ident.getLineNum();
        SymbolType symbolType;
        if(lbrack != null){
            symbolType = SymbolType.CONSTINTARRAY;
        }else{
            symbolType = SymbolType.CONSTINT;
        }
        //这里先冷处理ConstExp，都先不算数值，因此不用size那个构造函数
        ValSymbol valSymbol = new ValSymbol(name, symbolType, line);
        SymbolTableManager.addSymbol(valSymbol);
        if(constExp != null){
            constExp.check();
        }
        constInitVal.check();
    }

}
