package midend.ir;


import midend.ir.constant.ConstArray;
import midend.ir.constant.Constant;
import midend.ir.instruction.Alloca;
import midend.ir.instruction.GEP;
import midend.ir.instruction.Store;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.GlobalVariable;
import midend.ir.value.Value;

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
    /**
     * @param parent 基本块
     * @param base 基地址（是一个指针）
     * @param indices 变长索引
     * @return 一个新的指针
     */
    public GEP buildGEP(BasicBlock parent, Value base, Value... indices) {
        int nameNum = nameNumCount++;
        GEP ans;
        if (indices.length == 1) {
            ans = new GEP(nameNum, parent, base, indices[0]);
        }
        else {
            ans = new GEP(nameNum, parent, base, indices[0], indices[1]);
        }
        parent.insertTail(ans);
        return ans;
    }
    /**
     * @param parent 基本块
     * @param content 存储内容
     * @param addr 地址
     */
    public void buildStore(BasicBlock parent, Value content, Value addr) {
        Store ans = new Store(parent, content, addr);
        parent.insertTail(ans);
    }

}
