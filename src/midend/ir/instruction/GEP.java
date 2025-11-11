package midend.ir.instruction;

import midend.ir.type.ArrayType;
import midend.ir.type.IntegerType;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class GEP extends Instruction {
    private final ValueType baseType;

    /**
     * 函数参数数组寻址
     */
    public GEP(int nameNum, BasicBlock parent, Value base, Value firstIndex) {
        super("%p" + nameNum, (PointerType) base.getValueType(), parent, base, firstIndex);
        this.baseType = ((PointerType) base.getValueType()).getPointeeType();
    }
    /**
    正常寻址，一维数组，firstIndex为0，secondIndex为具体索引
     例如：数组a[10]，寻址a[3]，firstIndex=0，secondIndex=3
     */
    public GEP(int nameNum, BasicBlock parent, Value base, Value firstIndex, Value secondIndex) {
        super("%p" + nameNum, new PointerType(new IntegerType()), parent, base, firstIndex, secondIndex);
        this.baseType = ((PointerType) base.getValueType()).getPointeeType();
    }

}
