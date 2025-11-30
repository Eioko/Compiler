import error.ErrorManager;
import frontend.ast.CompUnit;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.Parser;
import midend.ir.IrModule;
import utils.FileProcess;

import java.util.ArrayList;

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
            IrModule.getInstance().toMips();
        }

        FileProcess.flushAll();
        if(ErrorManager.isEmpty()){
            FileProcess.writeIrFile();
            writeMipsFile();
        }

        FileProcess.closeAll();
    }
}