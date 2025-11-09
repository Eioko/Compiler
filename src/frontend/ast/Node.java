package frontend.ast;

import midend.ir.IrBuilder;
import midend.ir.value.Value;
import midend.symbol.FuncSymbol;
import midend.ir.value.BasicBlock;

public class Node {
    //错误检查
    protected static int inLoop = 0;
    protected static FuncSymbol curFuncSymbol = null;
    //LLVM生成

    protected static BasicBlock curBlock = null;

    protected static final IrBuilder irBuilder = IrBuilder.getInstance();
    //继承属性
    protected static boolean global = false;

    //综合属性
    protected static Value valueUp;
}
