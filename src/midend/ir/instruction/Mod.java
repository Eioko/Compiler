package midend.ir.instruction;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Mod extends BinInstruction {
    public Mod(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }
}
