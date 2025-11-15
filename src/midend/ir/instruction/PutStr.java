package midend.ir.instruction;

import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class PutStr extends Instruction {
    public PutStr(BasicBlock parent, Value strAddr) {
        super("", new VoidType(), parent, strAddr);
    }

    public String toString() {
        return "call void @putstr(i8* " + getUsedValue(0).getName() + ")";
    }
}
