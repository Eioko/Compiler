package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBinary;
import backend.instruction.MipsLi;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.constant.ConstInt;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static utils.Configs.regAlloca;

public class Add extends BinInstruction {
    public Add(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }

    @Override
    public String toString() {
        return this.getName() + " = add nsw " + this.getValueType().toString() + " " +
                this.getUsedValue(0).getName() + ", " + this.getUsedValue(1).getName();
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
                int result = num1 + num2;
                mipsBlock.addInstruction(new MipsLi(dest, new MipsImm(result)));
            } else if(val1 instanceof ConstInt){
                MipsOperand src1 = val2.toSimpleReg(false, function, block, 0);
                loadMemToReg(val2, src1, block, function);
                MipsImm src2 = new MipsImm(((ConstInt) val1).getNumber());
                mipsBlock.addInstruction(new backend.instruction.MipsBinary(MipsBinary.BinaryOp.ADDU, dest, src1, src2));
            } else{
                MipsOperand src1 = val1.toSimpleReg(false, function, block, 0);
                loadMemToReg(val1, src1, block, function);
                MipsOperand src2 = val2.toSimpleReg(true, function, block, 1);
                loadMemToReg(val2, src2, block, function);
                mipsBlock.addInstruction(new backend.instruction.MipsBinary(MipsBinary.BinaryOp.ADDU, dest, src1, src2));
            }
            saveRegToStack(this, dest ,block, function);
        }
        else{
            MipsOperand dest = this.toMipsOperand(false, function, block);
            if(val1 instanceof ConstInt && val2 instanceof ConstInt){
                int num1 = ((ConstInt)val1).getNumber();
                int num2 = ((ConstInt)val2).getNumber();
                int result = num1 + num2;
                mipsBlock.addInstruction(new MipsLi(dest, new MipsImm(result)));
            } else if (val1 instanceof ConstInt) {
                MipsOperand src1 = val2.toMipsOperand(false, function, block);
                MipsImm src2 = new MipsImm(((ConstInt) val1).getNumber());
                MipsBinary mipsAdd = new MipsBinary(MipsBinary.BinaryOp.ADDU, dest, src1, src2);
                mipsBlock.addInstruction(mipsAdd);
            } else {
                MipsOperand src1 = val1.toMipsOperand(false, function, block);
                MipsOperand src2 = val2.toMipsOperand(true, function, block);
                MipsBinary mipsAdd = new MipsBinary(MipsBinary.BinaryOp.ADDU, dest, src1, src2);
                mipsBlock.addInstruction(mipsAdd);
            }
        }
    }
}
