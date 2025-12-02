package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBeqz;
import backend.instruction.MipsJ;
import backend.operand.MipsLabel;
import backend.operand.MipsPhyReg;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.MipsModule.getValueToReg;

public class Br extends Instruction{
    public Br(BasicBlock parent, BasicBlock dest) {
        super("", new VoidType(), parent, dest);
    }
    public Br(BasicBlock parent, Value cond, BasicBlock thenBB, BasicBlock elseBB) {
        super("", new VoidType(), parent, cond, thenBB, elseBB);
    }
    @Override
    public String toString() {
        if (this.getNumOfOperands() == 1) {
            return "br label %" + this.getUsedValue(0).getName();
        } else {
            return "br i1 " + this.getUsedValue(0).getName() + ", label %" +
                    this.getUsedValue(1).getName() + ", label %" +
                    this.getUsedValue(2).getName();
        }
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();

        if (this.getNumOfOperands() == 1) {
            // 无条件跳转
            BasicBlock dest = (BasicBlock) this.getUsedValue(0);
            mipsBlock.addInstruction(new backend.instruction.MipsJ(new MipsLabel(dest.getName())));
            mipsBlock.setTrueSucc(dest.getMipsBlock());
        } else {
            Value cond = this.getUsedValue(0);
            BasicBlock thenBB = (BasicBlock) this.getUsedValue(1);
            BasicBlock elseBB = (BasicBlock) this.getUsedValue(2);

            MipsPhyReg condReg = getValueToReg(cond, function);
            if (condReg == null) {
                condReg = new MipsPhyReg(backend.operand.MipsPhyReg.Register.T0);
                loadMemToReg(cond, condReg, block, function);
            }

            mipsBlock.addInstruction(new MipsBeqz(condReg, new MipsLabel(elseBB.getName())));
            mipsBlock.addInstruction(new MipsJ(new MipsLabel(thenBB.getName())));
            mipsBlock.setTrueSucc(thenBB.getMipsBlock());
            mipsBlock.setFalseSucc(elseBB.getMipsBlock());
        }
        mipsBlock.addInstruction(new backend.instruction.MipsEmpty());
    }
}
