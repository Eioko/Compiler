package midend.ir.instruction;

import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class PutInt extends Instruction{
    public PutInt(BasicBlock parent, Value intValue) {
        super("", new VoidType(), parent, intValue);
    }

    public String toString() {
        return "call void @putint(i32 " + getUsedValue(0).getName() + ")";
    }
}
