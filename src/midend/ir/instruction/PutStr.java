package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.*;
import backend.operand.MipsOperand;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.operand.MipsPhyReg.A0;
import static utils.Configs.regAlloca;

public class PutStr extends Instruction {
    public PutStr(BasicBlock parent, Value strAddr) {
        super("", new VoidType(), parent, strAddr);
    }

    public String toString() {
        return "call void @putstr(i8* " + getUsedValue(0).getName() + ")";
    }

    public void toMips(BasicBlock block, Function function){
        MipsBlock mipsBlock = block.getMipsBlock();
        Value p = this.getUsedValue(0);
        MipsOperand toPrint = A0;
        if(!regAlloca){
            loadMemToReg(p, toPrint, block, function);
            mipsBlock.addInstruction(new MipsSyscall(4));
            mipsBlock.addInstruction(new MipsEmpty());
        }else{
            MipsOperand src = p.toMipsOperand(true, function, block);
            MipsMove mipsMove = new MipsMove(toPrint, src);
            mipsBlock.addInstruction(mipsMove);
            mipsBlock.addInstruction(new MipsSyscall(4));
            mipsBlock.addInstruction(new MipsEmpty());
        }
    }
}
