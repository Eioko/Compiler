package midend.ir.instruction;

import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Add extends BinInstruction {
    public Add(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }

    @Override
    public String toString() {
        return this.getName() + " = add nsw " + this.getValueType().toString() + " " +
                this.getUsedValue(0).getName() + ", " + this.getUsedValue(1).getName();
    }
}
