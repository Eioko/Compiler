package frontend.parser;

import error.ErrorType;
import error.SysyError;
import frontend.ast.CompUnit;
import frontend.ast.block.Block;
import frontend.ast.decl.*;
import frontend.ast.exp.ConstExp;
import frontend.ast.exp.Exp;
import frontend.ast.func.FuncDef;
import frontend.ast.func.FuncFParams;
import frontend.ast.func.FuncType;
import frontend.ast.func.MainFuncDef;
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
    private static final Map<TokenType, ErrorType> parserErrorMap = Map.of(
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
        ErrorType errorType = parserErrorMap.get(expected);
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

    /*
     CompUnit → {Decl} {FuncDef} MainFuncDef
     */
    private boolean startsDecl(){
        TokenType t0 = tokenStream.peek(0).getTokenType();
        if(t0 == TokenType.CONSTTK || t0 == TokenType.STATICTK){
            return true;
        }
        if(t0 == TokenType.INTTK){
            TokenType t1 = tokenStream.peek(1).getTokenType();
            TokenType t2 = tokenStream.peek(2).getTokenType();
            if(t1 == TokenType.MAINTK && t2 == TokenType.LPARENT){
                return false;
            }
            if(t1 == TokenType.IDENFR && t2 == TokenType.LPARENT){
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean startsFuncDef(){
        if(startsMainFuncDef()){
            return false;
        }
        TokenType t0 = tokenStream.peek(0).getTokenType();
        TokenType t1 = tokenStream.peek(1).getTokenType();
        TokenType t2 = tokenStream.peek(2).getTokenType();
        return (t0 == TokenType.INTTK || t0 == TokenType.VOIDTK)
                && t1 == TokenType.IDENFR
                && t2 == TokenType.LPARENT;
    }

    private boolean startsMainFuncDef(){
        TokenType t0 = tokenStream.peek(0).getTokenType();
        TokenType t1 = tokenStream.peek(1).getTokenType();
        TokenType t2 = tokenStream.peek(2).getTokenType();
        return t0==TokenType.INTTK && t1==TokenType.MAINTK && t2==TokenType.LPARENT;
    }

    private CompUnit parseCompUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef;
        while(startsDecl()){
            decls.add(parseDecl());
        }
        while(startsFuncDef()){
            funcDefs.add(parseFuncDef());
        }
        if(!startsMainFuncDef()){
            throw new RuntimeException("Not any Main!");
        }
        mainFuncDef = parseMainFuncDef();
        return new CompUnit(decls,funcDefs,mainFuncDef);
    }
    /*
        Decl → ConstDecl | VarDecl
    */
    private Decl parseDecl() {
        if(tokenStream.peek(0).getTokenType()==TokenType.CONSTTK){
            return new Decl(parseConstDecl());
        }else{
            return new Decl(parseVarDecl());
        }
    }
    /*
    ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
     */
    private ConstDecl parseConstDecl() {
        Token constToken;
        Token intToken;
        ConstDef constDef;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        Token semicnToken;

        constToken = expect(TokenType.CONSTTK);
        intToken = expect(TokenType.INTTK);
        constDef = parseConstDef();
        while (tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
            Token comma1 = expect(TokenType.COMMA);
            commaTokens.add(comma1);
            ConstDef constDef1 = parseConstDef();
            constDefs.add(constDef1);
        }
        semicnToken = expect(TokenType.SEMICN);

        return new ConstDecl(constToken, intToken, constDef, commaTokens, constDefs, semicnToken);
    }
    /*
     ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
     */
    private ConstDef parseConstDef() {
        Token ident;
        Token lbracket = null;
        ConstExp constExp = null;
        Token rbracket = null;
        Token assignToken;
        ConstInitVal constInitVal;

        ident = expect(TokenType.IDENFR);
        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.ASSIGN){
            lbracket = expect(TokenType.LBRACK);
            constExp = parseConstExp();
            rbracket = expect(TokenType.RBRACK);
        }
        assignToken = expect(TokenType.ASSIGN);
        constInitVal = parseConstInitVal();

        return new ConstDef(ident, lbracket, constExp, rbracket, assignToken, constInitVal);
    }
    /*
     ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
     */
    private ConstInitVal parseConstInitVal() {
        ConstExp constExp0;

        Token lbrace;
        ConstExp constExp1 = null;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<ConstExp> constExps = new ArrayList<>();
        Token rbrace;

        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.LBRACE){
            constExp0 = parseConstExp();
            return new ConstInitVal(constExp0);
        }else{
            lbrace = expect(TokenType.LBRACE);
            if(tokenStream.getCurrentToken().getTokenType()!=TokenType.RBRACE){
                constExp1 = parseConstExp();
                while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA){
                    Token comma1 = expect(TokenType.COMMA);
                    commaTokens.add(comma1);
                    ConstExp constExp = parseConstExp();
                    constExps.add(constExp);
                }
            }
            rbrace = expect(TokenType.RBRACE);
            return new ConstInitVal(lbrace, constExp1, commaTokens, constExps, rbrace);
        }
    }
    /*
    VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
     */
    private VarDecl parseVarDecl() {
        Token staticToken = null;
        Token intToken;
        VarDef varDef;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        Token semicnToken;

        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.INTTK) {
            staticToken = expect(TokenType.STATICTK);
        }
        intToken = expect(TokenType.INTTK);
        varDef = parseVarDef();
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
            Token comma1 = expect(TokenType.COMMA);
            commaTokens.add(comma1);
            VarDef varDef1 = parseVarDef();
            varDefs.add(varDef1);
        }
        semicnToken = expect(TokenType.SEMICN);
        return new VarDecl(staticToken, intToken, varDef, commaTokens, varDefs, semicnToken);
    }
    /*
    VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
     */
    private VarDef parseVarDef() {
        Token ident;
        Token lbrack = null;
        ConstExp constExp = null;
        Token rbrack= null;

        Token assignToken;
        InitVal initVal;

        ident = expect(TokenType.IDENFR);
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.LBRACK) {
            lbrack = expect(TokenType.LBRACK);
            constExp = parseConstExp();
            rbrack = expect(TokenType.RBRACK);
        }
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.ASSIGN) {
            assignToken = expect(TokenType.ASSIGN);
            initVal = parseInitVal();
            return new VarDef(ident, lbrack, constExp, rbrack, assignToken, initVal);
        }
        return new VarDef(ident, lbrack, constExp, rbrack);
    }
    /*
    InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
     */
    private InitVal parseInitVal() {
        Exp exp0;

        Token lbrace;
        Exp exp1 = null;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<Exp> exps = new ArrayList<>();
        Token rbrace;
        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.LBRACE) {
            exp0 = parseExp();
            return new InitVal(exp0);
        }
        lbrace = expect(TokenType.LBRACE);
        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.RBRACE) {
            exp1 = parseExp();
            while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
                Token comma1 = expect(TokenType.COMMA);
                commaTokens.add(comma1);
                Exp exp2 = parseExp();
                exps.add(exp2);
            }
        }
        rbrace = expect(TokenType.RBRACE);
        return new InitVal(lbrace, exp1, commaTokens, exps, rbrace);
    }
    /*
     FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
     */
    private FuncDef parseFuncDef() {
        FuncType funcType;
        Token ident;
        Token lparent;
        FuncFParams funcFParams = null;
        Token rparent;
        Block block;

        funcType = parseFuncType();
        ident = expect(TokenType.IDENFR);
        lparent = expect(TokenType.LPARENT);
        if(tokenStream.getCurrentToken().getTokenType()!=TokenType.RPARENT){
            funcFParams = parseFuncFParams();
        }

        rparent = expect(TokenType.RPARENT);
        block = parseBlock();
        return new FuncDef(funcType, ident, lparent, funcFParams, rparent, block);
    }
    /*
    MainFuncDef → 'int' 'main' '(' ')' Block
     */
    private MainFuncDef parseMainFuncDef() {
        Token intToken;
        Token mainToken;
        Token lparen;
        Token rparen;
        Block block;

        intToken = expect(TokenType.INTTK);
        mainToken = expect(TokenType.MAINTK);
        lparen = expect(TokenType.LPARENT);
        rparen = expect(TokenType.RPARENT);
        block = parseBlock();

        return new MainFuncDef(intToken, mainToken, lparen, rparen, block);
    }
    /*
     FuncType → 'void' | 'int'
     */

}
