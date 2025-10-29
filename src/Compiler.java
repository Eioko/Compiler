import error.ErrorManager;
import frontend.ast.CompUnit;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.Parser;
import utils.FileProcess;

import java.util.ArrayList;

import static error.ErrorManager.isEmpty;
import static utils.FileProcess.printTokens;


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
        FileProcess.flushAll();
        FileProcess.closeAll();
    }
}