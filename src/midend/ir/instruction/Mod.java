package midend.ir.instruction;
import backend.component.MipsBlock;
import backend.instruction.MipsBinary;
import backend.instruction.MipsEmpty;
import backend.operand.MipsOperand;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.instruction.MipsBinary.BinaryOp.MOD;
import static utils.Configs.regAlloca;

public class Mod extends BinInstruction {
    public Mod(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = srem ");
        sb.append(this.getUsedValue(0).getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        sb.append(", ");
        sb.append(this.getUsedValue(1).getName());
        return sb.toString();
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();

        Value val1 = this.getUsedValue(0);
        Value val2 = this.getUsedValue(1);
        if(!regAlloca){
            MipsOperand dest = this.toSimpleReg(false, function, block, 2);
            MipsOperand src1 = val1.toSimpleReg(false, function, block, 0);
            MipsOperand src2 = val2.toSimpleReg(false, function, block, 1);

            loadMemToReg(val1, src1, block, function);
            loadMemToReg(val2, src2, block, function);
            // 这里把除法和后面的move from HI/LO合并了, 不再加mfhi指令
            mipsBlock.addInstruction(new MipsBinary(MOD, dest, src1, src2));
            saveRegToStack(this, dest ,block, function);
        }
        else{
            MipsOperand dest = this.toMipsOperand(false, function, block);
            MipsOperand src1 = val1.toMipsOperand(false, function, block);
            MipsOperand src2 = val2.toMipsOperand(false, function, block);
            mipsBlock.addInstruction(new MipsBinary(MOD, dest, src1, src2));
        }
    }
}
