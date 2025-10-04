package frontend.ast;

import frontend.ast.decl.Decl;
import frontend.ast.decl.VarDecl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;

import java.util.ArrayList;

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
}
