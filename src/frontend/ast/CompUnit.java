package frontend.ast;

import frontend.ast.decl.Decl;
import frontend.ast.decl.VarDecl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import midend.ir.type.IntegerType;
import midend.ir.type.StringType;
import midend.ir.type.VoidType;
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

    public void buildIr(){

        irBuilder.buildDeclare("getint", new IntegerType(), new ArrayList<>());
        irBuilder.buildDeclare("putint", new VoidType(), new ArrayList<>(){
            {
                add(new IntegerType());
            }
        });
        irBuilder.buildDeclare("putch", new VoidType(), new ArrayList<>(){
            {
                add(new IntegerType());
            }
        });
        irBuilder.buildDeclare("putstr", new VoidType(), new ArrayList<>(){
            {
                add(new StringType());
            }
        });
        for(Decl decl : decls){
            decl.buildIr();
        }
        for(FuncDef funcDef : funcDefs){
            funcDef.buildIr();
        }
        mainFuncDef.buildIr();
    }

}
