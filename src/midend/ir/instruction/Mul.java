package midend.ir.instruction;

import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Mul extends BinInstruction {
    public Mul(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = mul nsw ");
        sb.append(this.getUsedValue(0).getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        sb.append(", ");
        sb.append(this.getUsedValue(1).getName());
        return sb.toString();
    }
}
