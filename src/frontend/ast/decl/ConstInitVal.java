package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.lexer.Token;
import midend.ir.constant.ConstArray;
import midend.ir.constant.ConstInt;
import midend.ir.constant.Constant;

import java.util.ArrayList;

/*
 *  ConstInitVal → ConstExp
 *              | '{' [ ConstExp { ',' ConstExp } ] '}'
 */
public class ConstInitVal extends Node {

    private ConstExp constExp0;

    private Token lbrace;
    private ConstExp constExp1;
    private ArrayList<Token> commaTokens;
    private ArrayList<ConstExp> constExps;
    private Token rbrace;

    private int utype;

    public ConstInitVal(ConstExp constExp){
        this.constExp0 = constExp;
        this.utype = 0;
    }
    public ConstInitVal(Token lbrace,
                        ConstExp constExp,
                        ArrayList<Token> commaTokens,
                        ArrayList<ConstExp> constExps,
                        Token rbrace){
        this.lbrace = lbrace;
        this.constExp1 = constExp;
        this.commaTokens = commaTokens;
        this.constExps = constExps;
        this.rbrace = rbrace;
        this.utype = 1;
    }
    public void check(){
        if(utype == 0){
            constExp0.check();
        }else{
            if(constExp1!=null){
                constExp1.check();
                for(ConstExp c : constExps){
                    c.check();
                }
            }
        }
    }
    public void buildIr(){

        if(utype == 0){
            constExp0.buildIr();
        }else{
            ArrayList<Constant> elements = new ArrayList<>();
            if(constExp1!=null){
                constExp1.buildIr();
                elements.add((Constant) valueUp);
                for(ConstExp c : constExps){
                    c.buildIr();
                    elements.add((Constant) valueUp);
                }
            }
            // build constant array value
            valueUp = new ConstArray(elements);
        }
    }
}
