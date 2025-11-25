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

public class Sub extends BinInstruction {
    public Sub(int nameNum, BasicBlock parent, Value op1, Value op2) {
        super(nameNum, parent, op1, op2);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = sub nsw ");
        sb.append(this.getUsedValue(0).getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        sb.append(", ");
        sb.append(this.getUsedValue(1).getName());
        return sb.toString();
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();

        MipsOperand dest = this.toMipsOperand(false, function, block, 2);
        Value val1 = getUsedValue(0);
        Value val2 = getUsedValue(1);
        if(val1 instanceof ConstInt && val2 instanceof ConstInt){
            int num1 = ((ConstInt)val1).getNumber();
            int num2 = ((ConstInt)val2).getNumber();
            int result = num1 - num2;
            mipsBlock.addInstruction(new MipsLi(dest, new MipsImm(result)));
        } else if(val2 instanceof ConstInt){
            MipsOperand src1 = val1.toMipsOperand(false, function, block, 0);
            int a = ((ConstInt) val2).getNumber();
            ConstInt negConst = new ConstInt(-a);
            MipsOperand src2 = negConst.toMipsOperand(true, function, block, 1);
            mipsBlock.addInstruction(new backend.instruction.MipsBinary(MipsBinary.BinaryOp.ADDU, dest, src1, src2));
        } else{
            MipsOperand src1 = val1.toMipsOperand(false, function, block, 0);
            //下面这条也可以是false
            MipsOperand src2 = val2.toMipsOperand(true, function, block, 1);
            mipsBlock.addInstruction(new backend.instruction.MipsBinary(MipsBinary.BinaryOp.SUBU, dest, src1, src2));
        }
    }
}
