package midend.ir.instruction;

import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Br extends Instruction{
    public Br(BasicBlock parent, BasicBlock dest) {
        super("", new VoidType(), parent, dest);
    }
    public Br(BasicBlock parent, Value cond, BasicBlock thenBB, BasicBlock elseBB) {
        super("", new VoidType(), parent, cond, thenBB, elseBB);
    }
    @Override
    public String toString() {
        if (this.getNumOfOperands() == 1) {
            return "br label %" + this.getUsedValue(0).getName();
        } else {
            return "br i1 " + this.getUsedValue(0).getName() + ", label %" +
                    this.getUsedValue(1).getName() + ", label %" +
                    this.getUsedValue(2).getName();
        }
    }
}
