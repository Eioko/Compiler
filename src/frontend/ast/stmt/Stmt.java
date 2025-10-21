package frontend.ast.stmt;

import error.ErrorManager;
import error.ErrorType;
import error.SysyError;
import frontend.ast.Node;
import frontend.ast.block.Block;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import midend.symbol.Symbol;
import midend.symbol.SymbolTableManager;
import midend.symbol.SymbolType;

import java.util.ArrayList;

import static error.ErrorManager.errors;
import static error.ErrorType.UNDEFINED_IDENTIFIER;

/**
 * 语句统一节点，分支：
 * 1) LVal '=' Exp ';'
 * 2) [Exp] ';'
 * 3) Block
 * 4) if '(' Cond ')' Stmt [else Stmt]
 * 5) for '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
 * 6) break ';'
 * 7) continue ';'
 * 8) return [Exp] ';'
 * 9) printf '(' StringConst {',' Exp} ')' ';'
 *
 * utype:
 *   0 = ASSIGN
 *   1 = EXP_OR_EMPTY
 *   2 = BLOCK
 *   3 = IF
 *   4 = FOR
 *   5 = BREAK
 *   6 = CONTINUE
 *   7 = RETURN
 *   8 = PRINTF
 */
public class Stmt extends Node {
    public static final int ASSIGN       = 0;
    public static final int EXP_OR_EMPTY = 1;
    public static final int BLOCK_TYPE   = 2;
    public static final int IF           = 3;
    public static final int FOR          = 4;
    public static final int BREAK        = 5;
    public static final int CONTINUE     = 6;
    public static final int RETURN       = 7;
    public static final int PRINTF       = 8;

    private int utype;

    // (0) 赋值
    private LVal assignLVal;
    private Token assignToken;
    private Exp assignExp;
    private Token assignSemicn;

    // (1) [Exp] ';'
    private Exp exprStmtExp;      // 可为 null
    private Token exprStmtSemicn;

    // (2) Block
    private Block block;

    // (3) if
    private Token ifToken;
    private Token ifLparen;
    private Cond ifCond;
    private Token ifRparen;
    private Stmt thenStmt;
    private Token elseToken;  // 可为 null
    private Stmt elseStmt;    // 可为 null

    // (4) for
    private Token forToken;
    private Token forLparen;
    private ForStmt forInit;     // 可为 null
    private Token forFirstSemicn;
    private Cond forCond;          // 可为 null
    private Token forSecondSemicn;
    private ForStmt forUpdate;   // 可为 null
    private Token forRparen;
    private Stmt forBody;

    // (5) break
    private Token breakToken;
    private Token breakSemicn;

    // (6) continue
    private Token continueToken;
    private Token continueSemicn;

    // (7) return
    private Token returnToken;
    private Exp returnExp;       // 可为 null
    private Token returnSemicn;

    // (8) printf
    private Token printfToken;
    private Token printfLparen;
    private Token stringConstToken;
    private ArrayList<Token> printfCommaTokens;
    private ArrayList<Exp> printfArgs;
    private Token printfRparen;
    private Token printfSemicn;

    // 0) 赋值语句
    public Stmt(LVal lVal, Token assignToken, Exp exp, Token semicn) {
        this.utype = ASSIGN;
        this.assignLVal = lVal;
        this.assignToken = assignToken;
        this.assignExp = exp;
        this.assignSemicn = semicn;
    }

    // 1) [Exp] ';'  (exp 可为 null 表示空语句)
    public Stmt(Exp exp, Token semicn) {
        this.utype = EXP_OR_EMPTY;
        this.exprStmtExp = exp;
        this.exprStmtSemicn = semicn;
    }

    // 2) Block
    public Stmt(Block block) {
        this.utype = BLOCK_TYPE;
        this.block = block;
    }

    // 3) if (...) stmt [else stmt]
    public Stmt(Token ifToken,
                Token lparen,
                Cond cond,
                Token rparen,
                Stmt thenStmt,
                Token elseToken,
                Stmt elseStmt) {
        this.utype = IF;
        this.ifToken = ifToken;
        this.ifLparen = lparen;
        this.ifCond = cond;
        this.ifRparen = rparen;
        this.thenStmt = thenStmt;
        this.elseToken = elseToken;
        this.elseStmt = elseStmt;
    }

    // 4) for '(' [init] ';' [cond] ';' [update] ')' body
    public Stmt(Token forToken,
                Token lparen,
                ForStmt init,
                Token firstSemicn,
                Cond cond,
                Token secondSemicn,
                ForStmt update,
                Token rparen,
                Stmt body) {
        this.utype = FOR;
        this.forToken = forToken;
        this.forLparen = lparen;
        this.forInit = init;
        this.forFirstSemicn = firstSemicn;
        this.forCond = cond;
        this.forSecondSemicn = secondSemicn;
        this.forUpdate = update;
        this.forRparen = rparen;
        this.forBody = body;
    }

    // 5 or 6) break continue;
    public Stmt(Token Token, Token Semicn) {
        if(Token.getTokenType() == TokenType.BREAKTK){
            this.utype = BREAK;
            this.breakToken = Token;
            this.breakSemicn = Semicn;
        }else if(Token.getTokenType() == TokenType.BREAKTK){
            this.utype = CONTINUE;
            this.continueToken = Token;
            this.continueSemicn = Semicn;
        }

    }


    // 7) return [exp] ;
    public Stmt(Token returnToken, Exp returnExp, Token returnSemicn) {
        this.utype = RETURN;
        this.returnToken = returnToken;
        this.returnExp = returnExp;
        this.returnSemicn = returnSemicn;
    }

    // 8) printf '(' StringConst {',' Exp} ')' ';'
    public Stmt(Token printfToken,
                Token lparen,
                Token stringConstToken,
                ArrayList<Token> commaTokens,
                ArrayList<Exp> args,
                Token rparen,
                Token semicn) {
        this.utype = PRINTF;
        this.printfToken = printfToken;
        this.printfLparen = lparen;
        this.stringConstToken = stringConstToken;
        this.printfCommaTokens = commaTokens;
        this.printfArgs = args;
        this.printfRparen = rparen;
        this.printfSemicn = semicn;
    }

    public void check(){
        if(isAssign()){
            String name = assignLVal.getIdentToken().getTokenContent();
            int line = assignLVal.getIdentToken().getLineNum();
            Symbol symbol = SymbolTableManager.getSymbol(name, line);

            if(symbol != null){
                //这里不处理未定义错误，防止重复记录一个错误
                if(symbol.isConst()){
                    errors.add(new SysyError(ErrorType.ASSIGN_TO_CONST, line));
                    return;
                }
            }
            assignLVal.check();
            assignExp.check();
        }else if(isExprOrEmpty()){
            if(exprStmtExp!=null){
                exprStmtExp.check();
            }
        }else if(isBlock()){
            SymbolTableManager.createSonTable();
            //这里还要判断函数里面的return对不对，可能有问题
            block.check(false, null);
            SymbolTableManager.gotoFatherTable();
        }else if(isIf()){
            ifCond.check();
            thenStmt.check();
            if(elseStmt!=null){
                elseStmt.check();
            }
        }else if(isFor()){
            if(forInit!=null){
                forInit.check();
            }
            if(forCond!=null){
                forCond.check();
            }
            if(forUpdate!=null){
                forUpdate.check();
            }
            inLoop++;
            forBody.check();
            inLoop--;
        }else if(isBreak()){
            if(inLoop<=0){
                errors.add(new SysyError(ErrorType.LOOP_CONTROL_OUTSIDE_LOOP, breakToken.getLineNum()));
            }
        }else if(isContinue()){
            if(inLoop<=0){
                errors.add(new SysyError(ErrorType.LOOP_CONTROL_OUTSIDE_LOOP, breakToken.getLineNum()));
            }
        }else if(isReturn()){
            if(curFuncSymbol.getSymbolType()== SymbolType.VOIDFUNC){
                if(returnExp!=null){
                    //这里还checkExp吗？？
                    errors.add(new SysyError(ErrorType.INVALID_RETURN_IN_VOID_FUNCTION, returnToken.getLineNum()));
                }
            }
            if(returnExp!=null){
                returnExp.check();
            }

        }else if(isPrintf()){
            String constStr = stringConstToken.getTokenContent();
            int format = 0;
            for(int i=0;i<constStr.length()-1;i++){
                if(constStr.charAt(i)=='%'&&constStr.charAt(i+1)=='d'){
                    format++;
                }
            }
            int real = printfArgs.size();
            if(real != format){
                errors.add(new SysyError(ErrorType.PRINTF_FORMAT_ARG_MISMATCH, printfToken.getLineNum()));
            }
            for(Exp exp: printfArgs){
                exp.check();
            }
        }
    }

    public int getUtype() { return utype; }

    public boolean isAssign()       { return utype == ASSIGN; }
    public boolean isExprOrEmpty()  { return utype == EXP_OR_EMPTY; }
    public boolean isBlock()        { return utype == BLOCK_TYPE; }
    public boolean isIf()           { return utype == IF; }
    public boolean isFor()          { return utype == FOR; }
    public boolean isBreak()        { return utype == BREAK; }
    public boolean isContinue()     { return utype == CONTINUE; }
    public boolean isReturn()       { return utype == RETURN; }
    public boolean isPrintf()       { return utype == PRINTF; }

    // 赋值
    public LVal getAssignLVal() { return assignLVal; }
    public Token getAssignToken() { return assignToken; }
    public Exp getAssignExp() { return assignExp; }
    public Token getAssignSemicn() { return assignSemicn; }

    // [Exp] ';'
    public Exp getExprStmtExp() { return exprStmtExp; }
    public Token getExprStmtSemicn() { return exprStmtSemicn; }

    // Block
    public Block getBlock() { return block; }

    // If
    public Token getIfToken() { return ifToken; }
    public Token getIfLparen() { return ifLparen; }
    public Cond getIfCond() { return ifCond; }
    public Token getIfRparen() { return ifRparen; }
    public Stmt getThenStmt() { return thenStmt; }
    public Token getElseToken() { return elseToken; }
    public Stmt getElseStmt() { return elseStmt; }

    // For
    public Token getForToken() { return forToken; }
    public Token getForLparen() { return forLparen; }
    public ForStmt getForInit() { return forInit; }
    public Token getForFirstSemicn() { return forFirstSemicn; }
    public Cond getForCond() { return forCond; }
    public Token getForSecondSemicn() { return forSecondSemicn; }
    public ForStmt getForUpdate() { return forUpdate; }
    public Token getForRparen() { return forRparen; }
    public Stmt getForBody() { return forBody; }

    // Break
    public Token getBreakToken() { return breakToken; }
    public Token getBreakSemicn() { return breakSemicn; }

    // Continue
    public Token getContinueToken() { return continueToken; }
    public Token getContinueSemicn() { return continueSemicn; }

    // Return
    public Token getReturnToken() { return returnToken; }
    public Exp getReturnExp() { return returnExp; }
    public Token getReturnSemicn() { return returnSemicn; }

    // Printf
    public Token getPrintfToken() { return printfToken; }
    public Token getPrintfLparen() { return printfLparen; }
    public Token getStringConstToken() { return stringConstToken; }
    public ArrayList<Token> getPrintfCommaTokens() { return printfCommaTokens; }
    public ArrayList<Exp> getPrintfArgs() { return printfArgs; }
    public Token getPrintfRparen() { return printfRparen; }
    public Token getPrintfSemicn() { return printfSemicn; }
}