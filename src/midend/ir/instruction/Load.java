package midend.ir.instruction;

import midend.ir.type.PointerType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Load extends Instruction {
    /**
     * 返回一个值，而不是指针
     * @param nameNum
     * @param pointerValue
     * @param parent
     */
    public Load(int nameNum, Value pointerValue, BasicBlock parent) {
        super("%v"+nameNum, ((PointerType)pointerValue.getValueType()).getPointeeType(), parent, pointerValue);
    }
}
