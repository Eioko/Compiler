package frontend.lexer;

public class Token {
    private final String tokenContent;
    private final TokenType tokenType;
    private final int lineNum;

    public Token(String tokenContent, TokenType tokenType, int lineNum) {
        this.tokenContent = tokenContent;
        this.tokenType = tokenType;
        this.lineNum = lineNum;
    }
    public String getTokenContent() {
        return tokenContent;
    }
    public TokenType getTokenType() {
        return tokenType;
    }
    public int getLineNum() {
        return lineNum;
    }
}
