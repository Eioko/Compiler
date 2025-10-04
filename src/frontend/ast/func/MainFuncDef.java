package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.lexer.Token;

/**
 * MainFuncDef -> 'int' 'main' '(' ')' Block
 */
public class MainFuncDef extends Node {
    private Token intToken;
    private Token mainToken;
    private Token lparenToken;
    private Token rparenToken;
    private Block block;

    public MainFuncDef(Token intToken,
                       Token mainToken,
                       Token lparenToken,
                       Token rparenToken,
                       Block block) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.lparenToken = lparenToken;
        this.rparenToken = rparenToken;
        this.block = block;
    }

    public Token getIntToken() {
        return intToken;
    }

    public Token getMainToken() {
        return mainToken;
    }

    public Block getBlock() {
        return block;
    }
}