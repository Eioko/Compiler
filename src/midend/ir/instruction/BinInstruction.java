package midend.ir.instruction;

import midend.ir.type.IntegerType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

public class BinInstruction extends Instruction{
    public BinInstruction(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super("%v"+nameNum, new IntegerType(), parent, op1, op2);
    }

    public void toMips(BasicBlock block, Function function) {}
}
