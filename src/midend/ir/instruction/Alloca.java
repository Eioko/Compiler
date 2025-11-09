package midend.ir.instruction;

import midend.ir.constant.ConstArray;
import midend.ir.constant.Constant;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;

public class Alloca extends Instruction{
    private ConstArray initVal = null;
    public Alloca(int num, ValueType valueType, BasicBlock block) {
        super("%p"+num, new PointerType(valueType), block);
    }
    public Alloca(int nameNum, ValueType allocatedType, BasicBlock parent, ConstArray initVal)
    {
        // 指针
        super("%p" + nameNum, new PointerType(allocatedType), parent);
        this.initVal = initVal;
    }
    public Alloca(int nameNum, ValueType allocatedType, BasicBlock parent, Constant initVal)
    {
        // 指针
        super("%p" + nameNum, new PointerType(allocatedType), parent);
        this.initVal = null;
    }

}

