package frontend.ast.exp;

import frontend.lexer.Token;

import java.util.ArrayList;

/**
 * MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
 */
public class MulExp extends ComptueExp {
    private UnaryExp firstUnary;
    private ArrayList<Token> opTokens;     // '*', '/', '%'
    private ArrayList<UnaryExp> otherUnaries;

    public MulExp(UnaryExp firstUnary,
                  ArrayList<Token> opTokens,
                  ArrayList<UnaryExp> otherUnaries) {
        this.firstUnary = firstUnary;
        this.opTokens = opTokens;
        this.otherUnaries = otherUnaries;
    }

    public UnaryExp getFirstUnary() { return firstUnary; }
    public ArrayList<Token> getOpTokens() { return opTokens; }
    public ArrayList<UnaryExp> getOtherUnaries() { return otherUnaries; }

    public int size() { return 1 + (otherUnaries == null ? 0 : otherUnaries.size()); }
}