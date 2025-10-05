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
            tokenStream.getCurTokenAndGo();
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
    private FuncType parseFuncType() {
        Token token = tokenStream.getCurrentToken();
        if(token.getTokenType()==TokenType.VOIDTK || token.getTokenType()==TokenType.INTTK){
            tokenStream.getCurTokenAndGo();
            return new FuncType(token);
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
        return new FuncFParams(funcFParam, commaTokens, funcFParams);
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
        return new FuncFParam(intToken, ident, lbrack, rbrack);
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
        return new Block(lbrace, items, rbrace);
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
            Block block = parseBlock();
            return new Stmt(block);
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
            return new Stmt(ifToken, lparen, cond, rparen, thenStmt, elseToken, elseStmt);
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
            if(tokenStream.getCurrentToken().getTokenType()!=TokenType.RPARENT) {
                stepStmt = parseForStmt();
            }
            Token rparen = expect(TokenType.RPARENT);
            Stmt bodyStmt = parseStmt();
            return new Stmt(forToken, lparen, initStmt, firstSemicn, cond, secondSemicn, stepStmt, rparen, bodyStmt);
        }else if(currentType == TokenType.BREAKTK) {
            Token breakToken = expect(TokenType.BREAKTK);
            Token semicn = expect(TokenType.SEMICN);
            return new Stmt(breakToken, semicn);
        }else if(currentType == TokenType.CONTINUETK) {
            Token continueToken = expect(TokenType.CONTINUETK);
            Token semicn = expect(TokenType.SEMICN);
            return new Stmt(continueToken, semicn);
        }else if(currentType == TokenType.RETURNTK) {
            Token returnToken = expect(TokenType.RETURNTK);
            Exp exp = null;
            if(tokenStream.getCurrentToken().getTokenType()!=TokenType.SEMICN) {
                exp = parseExp();
            }
            Token semicn = expect(TokenType.SEMICN);
            return new Stmt(returnToken, exp, semicn);
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
            return new Stmt(printfToken, lparen, stringConst, commaTokens, exps, rparen, semicn);
        }else if(tokenStream.getCurrentToken().getTokenType()==TokenType.IDENFR){
            LVal lVal = parseLVal();
            Token assignToken = expect(TokenType.ASSIGN);
            Exp exp = parseExp();
            Token semicToken = expect(TokenType.SEMICN);
            return new Stmt(lVal, assignToken, exp, semicToken);
        }else{
            Exp exp1 = null;
            if(currentType == TokenType.SEMICN) {
                Token semicn = expect(TokenType.SEMICN);
                return new Stmt(exp1, semicn);
            }
            exp1 = parseExp();
            Token semicn = expect(TokenType.SEMICN);
            return new Stmt(exp1, semicn);
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
        return new ForStmt(lVal, assignToken, exp, commaTokens, lVals, assignTokens, exps);
    }
    /*
     *  Exp → AddExp 
     */
    private Exp parseExp() {
        AddExp addExp = parseAddExp();
        return new Exp(addExp);
    }
    /*
     *  Cond → LOrExp
     */
    private Cond parseCond() {
        LOrExp lOrExp = parseLOrExp();
        return new Cond(lOrExp);
    }
    /*
     *  LVal → Ident ['[' Exp ']'] 
     */
    private LVal parseLVal() {
        Token ident;
        Token lbrack = null;
        Exp exp = null;
        Token rbrack = null;

        ident = expect(TokenType.IDENFR);
        if(tokenStream.getCurrentToken().getTokenType()==TokenType.LBRACK) {
            lbrack = expect(TokenType.LBRACK);
            exp = parseExp();
            rbrack = expect(TokenType.RBRACK);
        }
        return new LVal(ident, lbrack, exp, rbrack);
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
            return new PrimaryExp(lparen, exp, rparen);
        }else if(currentType == TokenType.IDENFR) {
            LVal lVal = parseLVal();
            return new PrimaryExp(lVal);
        }else if(currentType == TokenType.INTCON) {
            Number number = parseNumber();
            return new PrimaryExp(number);
        }
        throw new RuntimeException("PrimaryExp Unexpected token type: " + currentType);
    }
    /*
     * Number → IntConst
     */
    private Number parseNumber() {
        Token intConst = expect(TokenType.INTCON);
        return new Number(intConst);
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
                if(tokenStream.getCurrentToken().getTokenType()!=TokenType.RPARENT) {
                    funcRParams = parseFuncRParams();
                }
                Token rparen = expect(TokenType.RPARENT);
                return new UnaryExp(identToken, lparen, funcRParams, rparen);
            }else{
                PrimaryExp primaryExp = parsePrimaryExp();
                return new UnaryExp(primaryExp);
            }
        }else if(currentType == TokenType.PLUS || currentType == TokenType.MINU || currentType == TokenType.NOT) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }else{
            PrimaryExp primaryExp = parsePrimaryExp();
            return new UnaryExp(primaryExp);
        }
    }
    /*
     * UnaryOp → '+' | '−' | '!'
     */
    private UnaryOp parseUnaryOp() {
        Token currentToken = tokenStream.getCurrentToken();
        TokenType currentType = currentToken.getTokenType();
        if(currentType == TokenType.PLUS || currentType == TokenType.MINU || currentType == TokenType.NOT) {
            tokenStream.getCurTokenAndGo();
            return new UnaryOp(currentToken);
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
        return new FuncRParams(exp, commaTokens, exps);
    }
    /*
     * MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     */
    private MulExp parseMulExp() {
        UnaryExp firstUnary = parseUnaryExp();
        ArrayList<Token> opTokens = new ArrayList<>();
        ArrayList<UnaryExp> otherUnaries = new ArrayList<>();

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.MULT
                || tokenStream.getCurrentToken().getTokenType()==TokenType.DIV
                || tokenStream.getCurrentToken().getTokenType()==TokenType.MOD) {
            Token opToken = tokenStream.getCurTokenAndGo();
            opTokens.add(opToken);
            UnaryExp unaryExp = parseUnaryExp();
            otherUnaries.add(unaryExp);
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

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.PLUS
                || tokenStream.getCurrentToken().getTokenType()==TokenType.MINU) {
            Token opToken = tokenStream.getCurTokenAndGo();
            opTokens.add(opToken);
            MulExp mulExp = parseMulExp();
            otherMuls.add(mulExp);
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

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.LSS
                || tokenStream.getCurrentToken().getTokenType()==TokenType.GRE
                || tokenStream.getCurrentToken().getTokenType()==TokenType.LEQ
                || tokenStream.getCurrentToken().getTokenType()==TokenType.GEQ) {
            Token opToken = tokenStream.getCurTokenAndGo();
            opTokens.add(opToken);
            AddExp addExp = parseAddExp();
            otherAdds.add(addExp);
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

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.EQL
                || tokenStream.getCurrentToken().getTokenType()==TokenType.NEQ) {
            Token opToken = tokenStream.getCurTokenAndGo();
            opTokens.add(opToken);
            RelExp relExp = parseRelExp();
            otherRels.add(relExp);
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

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.AND) {
            Token andToken = tokenStream.getCurTokenAndGo();
            andTokens.add(andToken);
            EqExp eqExp = parseEqExp();
            otherEqs.add(eqExp);
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

        while(tokenStream.getCurrentToken().getTokenType()==TokenType.OR) {
            Token orToken = tokenStream.getCurTokenAndGo();
            orTokens.add(orToken);
            LAndExp landExp = parseLAndExp();
            otherLAnds.add(landExp);
        }
        return new LOrExp(firstLAnd, orTokens, otherLAnds);
    }
    /*
     *  ConstExp → AddExp
     */
    private ConstExp parseConstExp() {
        AddExp addExp = parseAddExp();
        return new ConstExp(addExp);
    }
}
