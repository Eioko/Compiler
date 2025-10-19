package frontend.ast;

import frontend.ast.decl.Decl;
import frontend.ast.decl.VarDecl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import midend.symbol.SymbolTable;
import midend.symbol.SymbolTableManager;

import java.util.ArrayList;
/*
 CompUnit → {Decl} {FuncDef} MainFuncDef
 */
public class CompUnit extends Node{
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    public CompUnit(ArrayList<Decl> decls,
                    ArrayList<FuncDef> funcDefs,
                    MainFuncDef mainFuncDef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    public void check(){
        SymbolTableManager.init();
        for(Decl decl : decls){
            decl.check();
        }
        for(FuncDef funcDef : funcDefs){
            funcDef.check();
        }
        mainFuncDef.check();
    }
}
