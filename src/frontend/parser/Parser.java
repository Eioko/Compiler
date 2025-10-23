package frontend.parser;

import error.ErrorType;
import error.SysyError;
import frontend.ast.CompUnit;
import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.ast.decl.*;
import frontend.ast.exp.AddExp;
import frontend.ast.exp.Cond;
import frontend.ast.exp.ConstExp;
import frontend.ast.exp.EqExp;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LAndExp;
import frontend.ast.exp.LOrExp;
import frontend.ast.exp.LVal;
import frontend.ast.exp.MulExp;
import frontend.ast.exp.PrimaryExp;
import frontend.ast.exp.RelExp;
import frontend.ast.exp.UnaryExp;
import frontend.ast.exp.UnaryOp;
import frontend.ast.func.*;
import frontend.ast.stmt.ForStmt;
import frontend.ast.stmt.Stmt;
import frontend.ast.exp.Number;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;
import utils.FileProcess;

import java.util.ArrayList;
import java.util.Map;

import static error.ErrorManager.*;
import static utils.FileProcess.finish;

public class Parser {
    private final TokenStream tokenStream;

    public Parser(ArrayList<Token> tokens) {
        this.tokenStream = new TokenStream(tokens);
    }
    private static final Map<TokenType, ErrorType> parserErrorMap = Map.of(
            TokenType.SEMICN, ErrorType.MISSING_SEMICOLON,
            TokenType.RPARENT, ErrorType.MISSING_RIGHT_PARENTHESIS,
            TokenType.RBRACK, ErrorType.MISSING_RIGHT_BRACKET
    );
    private Token consume() {
        Token t = tokenStream.getCurTokenAndGo();
        FileProcess.bufferToken(t);
        return t;
    }

    private Token expect(TokenType expected) {
        Token rtnToken = tokenStream.getCurrentToken();
        if(rtnToken.getTokenType() == expected) {
            tokenStream.getCurTokenAndGo();
            FileProcess.bufferToken(rtnToken);
            return rtnToken;
        }
        ErrorType errorType = parserErrorMap.get(expected);
        if(errorType != null) {
            int lineNum = tokenStream.previous().getLineNum();
            addError(new SysyError(errorType, lineNum));
            if(expected == TokenType.SEMICN) {
                return new Token(";",expected,lineNum);
            }else if(expected == TokenType.RPARENT) {
                return new Token(")",expected,lineNum);
            }else if(expected == TokenType.RBRACK) {
                return new Token("]",expected,lineNum);
            }else{
                throw new RuntimeException("Unexpected token type: " + rtnToken.getTokenType());
            }
        }
        throw new RuntimeException("Unexpected token type: " + rtnToken.getTokenType());
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

    public CompUnit parseCompUnit() {
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
        return finish("CompUnit", new CompUnit(decls, funcDefs, mainFuncDef));
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

        return finish("ConstDecl", new ConstDecl(constToken, intToken, constDef, commaTokens, constDefs, semicnToken));
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

        return finish("ConstDef", new ConstDef(ident, lbracket, constExp, rbracket, assignToken, constInitVal));
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
            return finish("ConstInitVal", new ConstInitVal(constExp0));
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
            return finish("ConstInitVal", new ConstInitVal(lbrace, constExp1, commaTokens, constExps, rbrace));
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
        return finish("VarDecl", new VarDecl(staticToken, intToken, varDef, commaTokens, varDefs, semicnToken));
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
            return finish("VarDef", new VarDef(ident, lbrack, constExp, rbrack, assignToken, initVal));
        }
        return finish("VarDef", new VarDef(ident, lbrack, constExp, rbrack));
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
            return finish("InitVal", new InitVal(exp0));
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
        return finish("InitVal", new InitVal(lbrace, exp1, commaTokens, exps, rbrace));
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
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.INTTK){
            funcFParams = parseFuncFParams();
        }

        rparent = expect(TokenType.RPARENT);
        block = parseBlock();
        return finish("FuncDef", new FuncDef(funcType, ident, lparent, funcFParams, rparent, block));
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

        return finish("MainFuncDef", new MainFuncDef(intToken, mainToken, lparen, rparen, block));
    }
    /*
     FuncType → 'void' | 'int'
     */
    private FuncType parseFuncType() {
        Token token = tokenStream.getCurrentToken();
        if(token.getTokenType()==TokenType.VOIDTK || token.getTokenType()==TokenType.INTTK){
            token = consume();
            return finish("FuncType", new FuncType(token));
        }
        throw new RuntimeException("Unexpected token type: " + token.getTokenType());
    }
    /*
     * FuncFParams → FuncFParam { ',' FuncFParam }
     */
    private FuncFParams parseFuncFParams() {
        FuncFParam funcFParam;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();

        funcFParam = parseFuncFParam();
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
            Token comma1 = expect(TokenType.COMMA);
            commaTokens.add(comma1);
            FuncFParam funcFParam1 = parseFuncFParam();
            funcFParams.add(funcFParam1);
        }
        return finish("FuncFParams", new FuncFParams(funcFParam, commaTokens, funcFParams));
    }
    /*
     *  FuncFParam → BType Ident ['[' ']']
     */
    private FuncFParam parseFuncFParam() {
        Token intToken;
        Token ident;
        Token lbrack = null;
        Token rbrack = null;

        intToken = expect(TokenType.INTTK);
        ident = expect(TokenType.IDENFR);
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.LBRACK) {
            lbrack = expect(TokenType.LBRACK);
            rbrack = expect(TokenType.RBRACK);
        }
        return finish("FuncFParam", new FuncFParam(intToken, ident, lbrack, rbrack));
    }
    /*
     * Block -> '{' { Decl | Stmt } '}'
     */
    private Block parseBlock() {
        Token lbrace;
        ArrayList<Node> items = new ArrayList<>();
        Token rbrace;

        lbrace = expect(TokenType.LBRACE);
        while(tokenStream.getCurrentToken().getTokenType()!=TokenType.RBRACE) {
            if(startsDecl()) {
                items.add(parseDecl());
            }else{
                items.add(parseStmt());
            }
        }
        rbrace = expect(TokenType.RBRACE);
        return finish("Block", new Block(lbrace, items, rbrace));
    }    
    /*
    Stmt → LVal '=' Exp ';'
        | [Exp] ';'
        | Block
        | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        | 'break' ';'
        | 'continue' ';'
        | 'return' [Exp] ';'
        | 'printf''('StringConst {','Exp}')'';'
     */
    private Stmt parseStmt() {
        Token currentToken = tokenStream.getCurrentToken();
        TokenType currentType = currentToken.getTokenType();
        if(currentType == TokenType.LBRACE) {
            Block block =   parseBlock();
            return finish("Stmt", new Stmt(block));
        }else if(currentType == TokenType.IFTK) {
            Token ifToken = expect(TokenType.IFTK);
            Token lparen = expect(TokenType.LPARENT);
            Cond cond = parseCond();
            Token rparen = expect(TokenType.RPARENT);
            Stmt thenStmt = parseStmt();
            Token elseToken = null;
            Stmt elseStmt = null;
            if(tokenStream.getCurrentToken().getTokenType()==TokenType.ELSETK) {
                elseToken = expect(TokenType.ELSETK);
                elseStmt = parseStmt();
            }
            return finish("Stmt", new Stmt(ifToken, lparen, cond, rparen, thenStmt, elseToken, elseStmt));
        }else if(currentType == TokenType.FORTK) {
            Token forToken = expect(TokenType.FORTK);
            Token lparen = expect(TokenType.LPARENT);
            ForStmt initStmt = null;
            Cond cond = null;
            ForStmt stepStmt = null;
            if(tokenStream.getCurrentToken().getTokenType()!=TokenType.SEMICN) {
                initStmt = parseForStmt();
            }
            Token firstSemicn = expect(TokenType.SEMICN);
            if(tokenStream.getCurrentToken().getTokenType()!=TokenType.SEMICN) {
                cond = parseCond();
            }
            Token secondSemicn = expect(TokenType.SEMICN);
            if(tokenStream.getCurrentToken().getTokenType()==TokenType.IDENFR) {
                stepStmt = parseForStmt();
            }
            Token rparen = expect(TokenType.RPARENT);
            Stmt bodyStmt = parseStmt();
            return finish("Stmt", new Stmt(forToken, lparen, initStmt, firstSemicn, cond, secondSemicn, stepStmt, rparen, bodyStmt));
        }else if(currentType == TokenType.BREAKTK) {
            Token breakToken = expect(TokenType.BREAKTK);
            Token semicn = expect(TokenType.SEMICN);
            return finish("Stmt", new Stmt(breakToken, semicn));
        }else if(currentType == TokenType.CONTINUETK) {
            Token continueToken = expect(TokenType.CONTINUETK);
            Token semicn = expect(TokenType.SEMICN);
            return finish("Stmt", new Stmt(continueToken, semicn));
        }else if(currentType == TokenType.RETURNTK) {
            Token returnToken = expect(TokenType.RETURNTK);
            Exp exp = null;
            TokenType type = tokenStream.getCurrentToken().getTokenType();
            if(type == TokenType.IDENFR || type==TokenType.PLUS||type==TokenType.MINU||type==TokenType.NOT
                    ||type==TokenType.LPARENT||type==TokenType.INTCON) {
                exp = parseExp();
            }
            Token semicn = expect(TokenType.SEMICN);
            return finish("Stmt", new Stmt(returnToken, exp, semicn));
        }else if(currentType == TokenType.PRINTFTK) {
            Token printfToken = expect(TokenType.PRINTFTK);
            Token lparen = expect(TokenType.LPARENT);
            Token stringConst = expect(TokenType.STRCON);
            ArrayList<Token> commaTokens = new ArrayList<>();
            ArrayList<Exp> exps = new ArrayList<>();
            while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
                Token comma1 = expect(TokenType.COMMA);
                commaTokens.add(comma1);
                Exp exp = parseExp();
                exps.add(exp);
            }
            Token rparen = expect(TokenType.RPARENT);
            Token semicn = expect(TokenType.SEMICN);
            return finish("Stmt", new Stmt(printfToken, lparen, stringConst, commaTokens, exps, rparen, semicn));
        }else{
            int intitalIndex = tokenStream.getIndex();
            boolean meetAssign = false;
            for(int i=0; i+intitalIndex<tokenStream.getLength();i++){
                if(tokenStream.peek(i).getTokenType()==TokenType.ASSIGN) {

                    meetAssign = true;
                    break;
                }
                if(tokenStream.peek(i).getTokenType()==TokenType.SEMICN){
                    break;
                }
            }
            if(meetAssign){
                LVal lVal = parseLVal();
                Token assignToken = expect(TokenType.ASSIGN);
                Exp exp = parseExp();
                Token semicToken = expect(TokenType.SEMICN);
                Stmt stmt= new Stmt(lVal, assignToken, exp, semicToken);
                return finish("Stmt", stmt);
            }else{
                if (currentType == TokenType.SEMICN) {
                    Token semicn = expect(TokenType.SEMICN);
                    return finish("Stmt", new Stmt((Exp) null, semicn));
                }
                Exp exp1 = parseExp();
                Token semicn = expect(TokenType.SEMICN);
                return finish("Stmt", new Stmt(exp1, semicn));
            }
        }
    }
    /*
     *  ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
     */
    private ForStmt parseForStmt() {
        LVal lVal;
        Token assignToken;
        Exp exp;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<LVal> lVals = new ArrayList<>();
        ArrayList<Token> assignTokens = new ArrayList<>();
        ArrayList<Exp> exps = new ArrayList<>();

        lVal = parseLVal();
        assignToken = expect(TokenType.ASSIGN);
        exp = parseExp();
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
            Token comma1 = expect(TokenType.COMMA);
            commaTokens.add(comma1);
            LVal lVal1 = parseLVal();
            lVals.add(lVal1);
            Token assign1 = expect(TokenType.ASSIGN);
            assignTokens.add(assign1);
            Exp exp1 = parseExp();
            exps.add(exp1);
        }
        return finish("ForStmt", new ForStmt(lVal, assignToken, exp, commaTokens, lVals, assignTokens, exps));
    }
    /*
     *  Exp → AddExp 
     */
    private Exp parseExp() {
        AddExp addExp = parseAddExp();
        return finish("Exp", new Exp(addExp));
    }
    /*
     *  Cond → LOrExp
     */
    private Cond parseCond() {
        LOrExp lOrExp = parseLOrExp();
        return finish("Cond", new Cond(lOrExp));
    }
    /*
     *  LVal → Ident ['[' Exp ']'] 
     */
    private LVal parseLVal() {
        Token ident = null;
        Token lbrack = null;
        Exp exp = null;
        Token rbrack = null;

        ident = expect(TokenType.IDENFR);
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.LBRACK) {
            lbrack = expect(TokenType.LBRACK);
            exp = parseExp();
            rbrack = expect(TokenType.RBRACK);
        }
        return finish("LVal", new LVal(ident, lbrack, exp, rbrack));
    }
    /*
     *  PrimaryExp → '(' Exp ')' | LVal | Number
     */
    private PrimaryExp parsePrimaryExp() {
        Token currentToken = tokenStream.getCurrentToken();
        TokenType currentType = currentToken.getTokenType();
        if(currentType == TokenType.LPARENT) {
            Token lparen = expect(TokenType.LPARENT);
            Exp exp = parseExp();
            Token rparen = expect(TokenType.RPARENT);
            return finish("PrimaryExp", new PrimaryExp(lparen, exp, rparen));
        }else if(currentType == TokenType.IDENFR) {
            LVal lVal = parseLVal();
            return finish("PrimaryExp", new PrimaryExp(lVal));
        }else if(currentType == TokenType.INTCON) {
            Number number = parseNumber();
            return finish("PrimaryExp", new PrimaryExp(number));
        }
        throw new RuntimeException("PrimaryExp Unexpected token type: " + currentType);
    }
    /*
     * Number → IntConst
     */
    private Number parseNumber() {
        Token intConst = expect(TokenType.INTCON);
        return finish("Number", new Number(intConst));
    }
    /*
     *  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp 
     */
    private UnaryExp parseUnaryExp() {
        Token currentToken = tokenStream.getCurrentToken();
        TokenType currentType = currentToken.getTokenType();
        if(currentType == TokenType.IDENFR) {
            if(tokenStream.peek(1).getTokenType()==TokenType.LPARENT) {
                Token identToken = expect(TokenType.IDENFR);
                Token lparen = expect(TokenType.LPARENT);
                FuncRParams funcRParams = null;
                TokenType type = tokenStream.getCurrentToken().getTokenType();
                if(type == TokenType.IDENFR || type==TokenType.PLUS||type==TokenType.MINU||type==TokenType.NOT
                        ||type==TokenType.LPARENT||type==TokenType.INTCON) {
                    funcRParams = parseFuncRParams();
                }
                Token rparen = expect(TokenType.RPARENT);
                return finish("UnaryExp", new UnaryExp(identToken, lparen, funcRParams, rparen));
            }else{
                PrimaryExp primaryExp = parsePrimaryExp();
                return finish("UnaryExp", new UnaryExp(primaryExp));
            }
        }else if(currentType == TokenType.PLUS || currentType == TokenType.MINU || currentType == TokenType.NOT) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return finish("UnaryExp", new UnaryExp(unaryOp, unaryExp));
        }else{
            PrimaryExp primaryExp = parsePrimaryExp();
            return finish("UnaryExp", new UnaryExp(primaryExp));
        }
    }
    /*
     * UnaryOp → '+' | '−' | '!'
     */
    private UnaryOp parseUnaryOp() {
        Token currentToken = tokenStream.getCurrentToken();
        TokenType currentType = currentToken.getTokenType();
        if(currentType == TokenType.PLUS || currentType == TokenType.MINU || currentType == TokenType.NOT) {
            currentToken = consume();
            return finish("UnaryOp", new UnaryOp(currentToken));
        }
        throw new RuntimeException("UnaryOp Unexpected token type: " + currentType);
    }
    /*
     *  FuncRParams → Exp { ',' Exp } 
     */
    private FuncRParams parseFuncRParams() {
        Exp exp;
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<Exp> exps = new ArrayList<>();

        exp = parseExp();
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.COMMA) {
            Token comma1 = expect(TokenType.COMMA);
            commaTokens.add(comma1);
            Exp exp1 = parseExp();
            exps.add(exp1);
        }
        return finish("FuncRParams", new FuncRParams(exp, commaTokens, exps));
    }
    /*
     * MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     */
    private MulExp parseMulExp() {
        UnaryExp firstUnary = parseUnaryExp();
        finish("MulExp", new MulExp(firstUnary, new ArrayList<>(), new ArrayList<>()));
        ArrayList<Token> opTokens = new ArrayList<>();
        ArrayList<UnaryExp> otherUnaries = new ArrayList<>();

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.MULT
                || tokenStream.getCurrentToken().getTokenType()==TokenType.DIV
                || tokenStream.getCurrentToken().getTokenType()==TokenType.MOD) {
            Token opToken = consume();
            opTokens.add(opToken);
            UnaryExp unaryExp = parseUnaryExp();
            otherUnaries.add(unaryExp);

            finish("MulExp", new MulExp(firstUnary, opTokens, otherUnaries));
        }
        return new MulExp(firstUnary, opTokens, otherUnaries);
    }  
    /*
     *  AddExp → MulExp | AddExp ('+' | '−') MulExp
     */
    private AddExp parseAddExp() {
        MulExp firstMul = parseMulExp();
        ArrayList<Token> opTokens = new ArrayList<>();
        ArrayList<MulExp> otherMuls = new ArrayList<>();

        finish("AddExp", new AddExp(firstMul, opTokens, otherMuls));

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.PLUS
                || tokenStream.getCurrentToken().getTokenType()==TokenType.MINU) {
            Token opToken = consume();
            opTokens.add(opToken);
            MulExp mulExp = parseMulExp();
            otherMuls.add(mulExp);

            finish("AddExp", new AddExp(firstMul, opTokens, otherMuls));
        }
        return new AddExp(firstMul, opTokens, otherMuls);
    }
    /*
     * RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     */
    private RelExp parseRelExp() {
        AddExp firstAdd = parseAddExp();
        ArrayList<Token> opTokens = new ArrayList<>();
        ArrayList<AddExp> otherAdds = new ArrayList<>();
        finish("RelExp", new RelExp(firstAdd, opTokens, otherAdds));

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.LSS
                || tokenStream.getCurrentToken().getTokenType()==TokenType.GRE
                || tokenStream.getCurrentToken().getTokenType()==TokenType.LEQ
                || tokenStream.getCurrentToken().getTokenType()==TokenType.GEQ) {
            Token opToken = consume();
            opTokens.add(opToken);
            AddExp addExp = parseAddExp();
            otherAdds.add(addExp);

            finish("RelExp", new RelExp(firstAdd, opTokens, otherAdds));
        }
        return new RelExp(firstAdd, opTokens, otherAdds);
    }
    /*
     *  EqExp → RelExp | EqExp ('==' | '!=') RelExp
     */
    private EqExp parseEqExp() {
        RelExp firstRel = parseRelExp();
        ArrayList<Token> opTokens = new ArrayList<>();
        ArrayList<RelExp> otherRels = new ArrayList<>();

        finish("EqExp", new EqExp(firstRel, opTokens, otherRels));
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.EQL
                || tokenStream.getCurrentToken().getTokenType()==TokenType.NEQ) {
            Token opToken = consume();
            opTokens.add(opToken);
            RelExp relExp = parseRelExp();
            otherRels.add(relExp);

            finish("EqExp", new EqExp(firstRel, opTokens, otherRels));
        }
        return new EqExp(firstRel, opTokens, otherRels);
    }
    /*
     *  LAndExp → EqExp | LAndExp '&&' EqExp
     */
    private LAndExp parseLAndExp() {
        EqExp firstEq = parseEqExp();
        ArrayList<Token> andTokens = new ArrayList<>();
        ArrayList<EqExp> otherEqs = new ArrayList<>();

        finish("LAndExp", new LAndExp(firstEq, andTokens, otherEqs));
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.AND) {
            Token andToken = expect(TokenType.AND);
            andTokens.add(andToken);
            EqExp eqExp = parseEqExp();
            otherEqs.add(eqExp);

            finish("LAndExp", new LAndExp(firstEq, andTokens, otherEqs));
        }
        return new LAndExp(firstEq, andTokens, otherEqs);
    }
    /*
     *  LOrExp → LAndExp | LOrExp '||' LAndExp 
     */
    private LOrExp parseLOrExp() {
        LAndExp firstLAnd = parseLAndExp();
        ArrayList<Token> orTokens = new ArrayList<>();
        ArrayList<LAndExp> otherLAnds = new ArrayList<>();

        finish("LOrExp", new LOrExp(firstLAnd, orTokens, otherLAnds));
        while(tokenStream.getCurrentToken().getTokenType()==TokenType.OR) {
            Token orToken = expect(TokenType.OR);
            orTokens.add(orToken);
            LAndExp landExp = parseLAndExp();
            otherLAnds.add(landExp);

            finish("LOrExp", new LOrExp(firstLAnd, orTokens, otherLAnds));
        }
        return new LOrExp(firstLAnd, orTokens, otherLAnds);
    }
    /*
     *  ConstExp → AddExp
     */
    private ConstExp parseConstExp() {
        AddExp addExp = parseAddExp();
        return finish("ConstExp", new ConstExp(addExp));
    }
}
