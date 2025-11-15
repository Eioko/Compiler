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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = load ");
        sb.append(this.getValueType().toString());
        sb.append(", ");
        sb.append(this.getUsedValue(0).getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        return sb.toString();
    }
}
