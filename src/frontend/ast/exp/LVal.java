package frontend.ast.exp;

import frontend.lexer.Token;

/**
 * LVal -> Ident ['[' Exp ']']
 */
public class LVal extends ComptueExp{
    private Token identToken;
    private Token lbrackToken; // 可为 null
    private Exp indexExp;      // 可为 null
    private Token rbrackToken; // 可为 null

    public LVal(Token identToken,
                Token lbrackToken,
                Exp indexExp,
                Token rbrackToken) {
        this.identToken = identToken;
        this.lbrackToken = lbrackToken;
        this.indexExp = indexExp;
        this.rbrackToken = rbrackToken;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public boolean hasIndex() {
        return indexExp != null;
    }

    public Token getLbrackToken() {
        return lbrackToken;
    }

    public Exp getIndexExp() {
        return indexExp;
    }

    public Token getRbrackToken() {
        return rbrackToken;
    }
}