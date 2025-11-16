package midend.ir.instruction;

import midend.ir.constant.ConstArray;
import midend.ir.constant.Constant;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;

public class Alloca extends Instruction{
    // 局部常量数组初始化值
    private ConstArray initVal = null;
    public Alloca(int numNum, ValueType allocatedType, BasicBlock parent) {
        super("%p"+numNum, new PointerType(allocatedType), parent);
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
        //??这里还没有实现
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = alloca ");
        sb.append(((PointerType)this.getValueType()).getPointeeType().toString());
        return sb.toString();
    }
    public ConstArray getInitVal(){
        return initVal;
    }
}

