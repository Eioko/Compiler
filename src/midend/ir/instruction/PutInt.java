package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsLi;
import backend.instruction.MipsMove;
import backend.instruction.MipsSyscall;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.operand.MipsPhyReg.A0;
import static backend.operand.MipsPhyReg.V0;
import static utils.Configs.regAlloca;

public class PutInt extends Instruction{
    public PutInt(BasicBlock parent, Value intValue) {
        super("", new VoidType(), parent, intValue);
    }

    public String toString() {
        return "call void @putint(i32 " + getUsedValue(0).getName() + ")";
    }

    public void toMips(BasicBlock block, Function function){
        MipsBlock mipsBlock = block.getMipsBlock();

        Value p = this.getUsedValue(0);
        MipsOperand toPrint = A0;
        if(!regAlloca){
            loadMemToReg(p, toPrint, block, function);
            MipsImm imm = new MipsImm(1);
            mipsBlock.addInstruction(new MipsLi(V0, imm));
            mipsBlock.addInstruction(new MipsSyscall());
            mipsBlock.addInstruction(new MipsEmpty());
        }else{
            MipsOperand src = p.toMipsOperand(false, function, block);
            MipsMove mipsMove = new MipsMove(toPrint, src);
            mipsBlock.addInstruction(mipsMove);
            MipsImm imm = new MipsImm(1);
            MipsLi li = new MipsLi(V0, imm);
            mipsBlock.addInstruction(li);
            mipsBlock.addInstruction(new MipsSyscall());
            mipsBlock.addInstruction(new MipsEmpty());
            // 防止寄存器分配消除掉这些move
            li.addUseReg(null, mipsMove.getDst());
        }
    }
}
