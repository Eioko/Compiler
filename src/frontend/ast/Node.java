package frontend.ast;

import midend.symbol.FuncSymbol;

public class Node {
    protected static int inLoop = 0;
    protected static FuncSymbol curFuncSymbol = null;
}
