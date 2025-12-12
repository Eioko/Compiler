import backend.optimize.PeepHole;
import backend.optimize.RegAllocator;
import error.ErrorManager;
import frontend.ast.CompUnit;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.Parser;
import midend.ir.IrModule;
import optimize.BuildCFG;
import optimize.DomAnalyzer;
import optimize.Mem2Reg;
import utils.FileProcess;

import java.util.ArrayList;

import static utils.Configs.*;
import static utils.FileProcess.printTokens;
import static utils.FileProcess.writeMipsFile;


public class Compiler {
    public static void main(String[] args) {
        FileProcess.initOutput();
        String source = FileProcess.readFile();
        Lexer lexer = Lexer.getInstance();
        ArrayList<Token> tokens = lexer.lexerAnalyze(source);
        if(ErrorManager.isEmpty()){
            printTokens(tokens);
        }
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parseCompUnit();
        compUnit.check();
        if(ErrorManager.isEmpty()){
            compUnit.buildIr();
            if(mem2reg){
                BuildCFG buildCFG = new BuildCFG();
                buildCFG.process();
                DomAnalyzer domAnalyzer = new DomAnalyzer();
                domAnalyzer.process();
                Mem2Reg mem2Reg = new Mem2Reg();
                mem2Reg.start();
            }
            IrModule.getInstance().toMips();
            if(regAlloca){
                RegAllocator regAllocator = new RegAllocator();
                regAllocator.process();
            }
            if(peepHole){
                PeepHole peepHoleOptimizer = new PeepHole();
                peepHoleOptimizer.process();
            }
        }

        //输出
        FileProcess.flushAll();
        if(ErrorManager.isEmpty()){
            FileProcess.writeIrFile();
            writeMipsFile();
        }

        FileProcess.closeAll();
    }
}