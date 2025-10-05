package frontend.parser;

import error.ErrorType;
import error.SysyError;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.Map;

public class Parser {
    private final TokenStream tokenStream;
    private final ArrayList<Token> tokens;
    private final ArrayList<SysyError> errors;

    public Parser(ArrayList<Token> tokens, ArrayList<SysyError> errors) {
        this.tokens = tokens;
        this.tokenStream = new TokenStream(tokens);
        this.errors = errors;
    }
    private static final Map<TokenType, ErrorType> parserMap = Map.of(
            TokenType.SEMICN, ErrorType.MISSING_SEMICOLON,
            TokenType.RPARENT, ErrorType.MISSING_RIGHT_PARENTHESIS,
            TokenType.RBRACK, ErrorType.MISSING_RIGHT_BRACKET
    );
    private Token expect(TokenType expected) {
        Token rtnToken = tokenStream.getCurrentToken();
        if(rtnToken.getTokenType() == expected) {
            tokenStream.getNextToken();
            return rtnToken;
        }
        ErrorType errorType = parserMap.get(expected);
        if(errorType != null) {
            int lineNum = rtnToken.getLineNum();
            errors.add(new SysyError(errorType, lineNum));
            if(expected == TokenType.SEMICN) {
                return new Token(";",expected,lineNum);
            }else if(expected == TokenType.RPARENT) {
                return new Token(")",expected,lineNum);
            }else if(expected == TokenType.RBRACK) {
                return new Token("]",expected,lineNum);
            }else{
                throw new RuntimeException("Unexpected token type: " + expected);
            }
        }
        throw new RuntimeException("Unexpected token type: " + expected);
    }

}
