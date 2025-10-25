import frontend.ast.CompUnit;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.parser.Parser;
import utils.FileProcess;

import java.util.ArrayList;


public class Compiler {
    public static void main(String[] args) {
        FileProcess.initOutput();
        String source = FileProcess.readFile();
        Lexer lexer = Lexer.getInstance();
        ArrayList<Token> tokens = lexer.lexerAnalyze(source);
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parseCompUnit();
        compUnit.check();
        FileProcess.flushAll();
        FileProcess.closeAll();
    }
}