package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBinary;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsLi;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.constant.ConstInt;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static utils.Configs.regAlloca;

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
    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();


        Value val1 = getUsedValue(0);
        Value val2 = getUsedValue(1);
        if(!regAlloca){
            MipsOperand dest = this.toSimpleReg(false, function, block, 2);
            if(val1 instanceof ConstInt && val2 instanceof ConstInt){
                int num1 = ((ConstInt)val1).getNumber();
                int num2 = ((ConstInt)val2).getNumber();
                int result = num1 * num2;
                mipsBlock.addInstruction(new MipsLi(dest, new MipsImm(result)));
            } else {
                MipsOperand src1 = val2.toSimpleReg(false, function, block, 0);
                MipsOperand src2 = val1.toSimpleReg(false, function, block, 1);
                loadMemToReg(val1, src1, block, function);
                loadMemToReg(val2, src2, block, function);
                //MUL能跑吗————能
                mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.MUL, dest, src1, src2));
            }
            saveRegToStack(this, dest ,block, function);
        }
        else{
            MipsOperand dest = this.toMipsOperand(false, function, block);
            MipsOperand src1 = val1.toMipsOperand(false, function, block);
            MipsOperand src2 = val2.toMipsOperand(false, function, block);
            mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.MUL, dest, src1, src2));
        }
    }
}
