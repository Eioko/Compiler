package midend.ir;


import midend.ir.constant.ConstArray;
import midend.ir.constant.Constant;
import midend.ir.instruction.Alloca;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.GlobalVariable;

public class IrBuilder {
    private static final IrBuilder irBuilder = new IrBuilder();
    private IrBuilder() {}
    public static IrBuilder getInstance() {
        return irBuilder;
    }

    public final IrModule irModule = IrModule.getInstance();

    private static int nameNumCount = 0;

    private static int strNumCount = 0;

    public GlobalVariable buildGlobalVariable(String name, Constant initVal, boolean isConst) {
        GlobalVariable globalVariable = new GlobalVariable(name, initVal, isConst);
        irModule.addGlobalVariable(globalVariable);
        return globalVariable;
    }
    public Alloca buildConstAlloca(ValueType allocatedType, BasicBlock parent, ConstArray initVal)
    {
        int nameNum = nameNumCount++;
        BasicBlock realParent = parent.getParent().getHeadBlock();
        Alloca ans = new Alloca(nameNum, allocatedType, realParent, initVal);
        realParent.insertHead(ans);
        return ans;
    }
    public Alloca buildConstAlloca(ValueType allocatedType, BasicBlock parent, Constant initVal)
    {
        int nameNum = nameNumCount++;
        BasicBlock realParent = parent.getParent().getHeadBlock();
        Alloca ans = new Alloca(nameNum, allocatedType, realParent, initVal);
        realParent.insertHead(ans);
        return ans;
    }
}
