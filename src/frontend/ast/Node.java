package frontend.ast;

import midend.ir.IrBuilder;
import midend.ir.type.DataType;
import midend.ir.value.Function;
import midend.ir.value.Value;
import midend.symbol.FuncSymbol;
import midend.ir.value.BasicBlock;
import java.util.ArrayList;

public class Node {
    //----------------------------------错误检查-----------------------------------------
    protected static int inLoop = 0;
    protected static FuncSymbol curFuncSymbol = null;

    //----------------------------------LLVM生成----------------------------------------
    protected static BasicBlock curBlock = null;

    protected static final IrBuilder irBuilder = IrBuilder.getInstance();
    //继承属性
    protected static boolean global = false;

    /**
     * 综合属性：返回值是一个 int ，其实本质上将其包装成 ConstInt 就可以通过 valueUp 返回，但是这样返回更加简便
     */
    protected static int valueIntUp = 0;
    /**
     * 综合属性：各种 buildIr 的结果(数组形式)如果会被其更高的节点应用，那么需要利用这个值进行通信
     */
    protected static ArrayList<Value> valueArrayUp = new ArrayList<>();

    //综合属性
    protected static Value valueUp;
    /**
        继承属性，现在的函数
     */
    protected static Function curfunc = null;
    /**
     * 继承属性，数组应该输出数组格式还是指针格式（函数参数）
     */
    protected static boolean arrayAsPtr = false;
    /**
     *  继承属性，函数调用的形参类型列表（向下传递）
     */
    protected static ArrayList<DataType> formalTypesDown = new ArrayList<>();
}
