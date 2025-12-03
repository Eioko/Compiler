package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsLi;
import backend.instruction.MipsMove;
import backend.instruction.MipsSyscall;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.operand.MipsPhyReg.A0;
import static backend.operand.MipsPhyReg.V0;
import static utils.Configs.optimize;

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
        if(!optimize){
            loadMemToReg(p, toPrint, block, function);
        }else{
            MipsOperand src = p.toMipsOperand(true, function, block);
            mipsBlock.addInstruction(new MipsMove(toPrint, src));
        }

        MipsImm imm = new MipsImm(4);
        mipsBlock.addInstruction(new MipsLi(V0, imm));
        mipsBlock.addInstruction(new MipsSyscall());
        mipsBlock.addInstruction(new MipsEmpty());
    }
}
