package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBinary;
import backend.operand.MipsOperand;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.instruction.MipsBinary.BinaryOp.DIV;

public class Div extends BinInstruction {
    public Div(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }
    @Override
    public String toString() {
        return this.getName() + " = sdiv " + this.getValueType().toString() + " " +
                this.getUsedValue(0).getName() + ", " + this.getUsedValue(1).getName();
    }
    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        MipsOperand dest = this.toMipsOperand(false, function, block, 2);
        Value val1 = this.getUsedValue(0);
        Value val2 = this.getUsedValue(1);

        MipsOperand src1 = val1.toMipsOperand(false, function, block, 0);
        MipsOperand src2 = val2.toMipsOperand(false, function, block, 1);

        loadMemToReg(val1, src1, block, function);
        loadMemToReg(val2, src2, block, function);
        // 这里把除法和后面的move from HI/LO合并了, 不再加mflo指令
        mipsBlock.addInstruction(new MipsBinary(DIV, dest, src1, src2));
        saveRegToStack(this, dest ,block, function);
    }
}
