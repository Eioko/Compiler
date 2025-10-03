import error.SysyError;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import utils.FileProcess;

import java.util.ArrayList;

public class Compiler {
    public static ArrayList<SysyError> errors = new ArrayList<SysyError>();
    public static void main(String[] args) {
        FileProcess.initOutput();
        String source = FileProcess.readFile();
        Lexer lexer = Lexer.getInstance();
        ArrayList<Token> tokens = lexer.lexerAnalyze(source,errors);
        if(errors.isEmpty()){
            FileProcess.printToken(tokens);
        }else{
            FileProcess.printErrors(errors);
        }
        FileProcess.closeAll();
    }
}