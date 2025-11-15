package midend.ir.instruction;

import midend.ir.type.DataType;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Ret extends Instruction {
    public Ret(BasicBlock parent) {
        super("",new VoidType(), parent);
    }

    public Ret(int nameNum, BasicBlock parent, Value returnValue) {
        super("%v"+nameNum, (DataType) returnValue.getValueType(), parent, returnValue);
    }

    @Override
    public String toString() {
        if(this.getValueType() instanceof VoidType) {
            return "ret void";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ret ");
        sb.append(this.getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        return sb.toString();
    }
}
