package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsSw;
import backend.operand.MipsOperand;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

public class Store extends Instruction {
    public Store(BasicBlock parent, Value value, Value addr) {
        super("", new VoidType(), parent, value, addr);
    }
    public String toString() {
        return "store " + getUsedValue(0).getValueType() + " " + getUsedValue(0).getName() + ", " +
                getUsedValue(1).getValueType() + " " + getUsedValue(1).getName();
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        Value value = getUsedValue(0);
        Value addr = getUsedValue(1);

        MipsOperand src = value.toMipsOperand(false, function, block, 0);
        MipsOperand destAddr = addr.toMipsOperand(false, function, block, 1);

        loadMemToReg(value, src, block, function);
        loadMemToReg(addr, destAddr, block, function);

        MipsOperand offset = new backend.operand.MipsImm(0);
        mipsBlock.addInstruction(new MipsSw(src, offset, destAddr));
        mipsBlock.addInstruction(new MipsEmpty());
    }
}
