package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.exp.ConstExp;
import frontend.ast.exp.Exp;
import frontend.lexer.Token;
import midend.ir.constant.ConstInt;
import midend.ir.value.Value;

import java.util.ArrayList;
/*
 InitVal → Exp |
            '{' [ Exp { ',' Exp } ] '}'
 */
public class InitVal extends Node {
    private Exp exp0;

    private Token lbrace;
    private Exp exp1;
    private ArrayList<Token> commaTokens;
    private ArrayList<Exp> exps;
    private Token rbrace;

    private int utype;

    public InitVal(Exp exp){
        this.exp0 = exp;
        utype = 0;
    }
    public InitVal(Token lbrace,
                   Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<Exp> exps,
                   Token rbrace){
        this.lbrace = lbrace;
        this.exp1 = exp;
        this.commaTokens = commaTokens;
        this.exps = exps;
        this.rbrace = rbrace;
        utype = 1;
    }

    public void check(){
        if(utype == 0){
            exp0.check();
        }else{
            if(exp1!=null){
                exp1.check();
                for(Exp c : exps) {
                    c.check();
                }
            }
        }
    }
    public void buildIr(){
        if(utype == 0){
            if(global){
                exp0.buildIr();
                valueUp = new ConstInt(valueIntUp);
            }else{
                exp0.buildIr();
            }
        }else{
            ArrayList<Value> arrayList = new ArrayList<>();
            if(exp1!=null){
                if(global){
                    exp1.buildIr();
                    arrayList.add(new ConstInt(valueIntUp));
                }else{
                    exp1.buildIr();
                    arrayList.add(valueUp);
                }
                for(Exp c : exps) {
                    if(global){
                        c.buildIr();
                        arrayList.add(new ConstInt(valueIntUp));
                    }else{
                        c.buildIr();
                        arrayList.add(valueUp);
                    }
                }
            }
            valueArrayUp = arrayList;
        }
    }
}
