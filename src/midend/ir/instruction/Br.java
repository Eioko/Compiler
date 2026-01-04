package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBeqz;
import backend.instruction.MipsJ;
import backend.operand.MipsLabel;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.constant.ConstInt;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static backend.MipsModule.getValueToReg;
import static utils.Configs.regAlloca;

public class Br extends Instruction{
    public boolean hasCond;
    public Br(BasicBlock parent, BasicBlock dest) {
        super("", new VoidType(), parent, dest);
        this.hasCond = false;
    }
    public Br(BasicBlock parent, Value cond, BasicBlock thenBB, BasicBlock elseBB) {
        super("", new VoidType(), parent, cond, thenBB, elseBB);
        this.hasCond = true;
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
            mipsBlock.addInstruction(new MipsJ(new MipsLabel(dest.getName())));
            mipsBlock.setTrueSucc(dest.getMipsBlock());
        } else {
            Value cond = this.getUsedValue(0);
            BasicBlock thenBB = (BasicBlock) this.getUsedValue(1);
            BasicBlock elseBB = (BasicBlock) this.getUsedValue(2);

            if(!regAlloca){
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
            else{
                if(cond instanceof ConstInt){
                    int num = ((ConstInt)cond).getNumber();
                    if(num != 0){
                        mipsBlock.addInstruction(new MipsJ(new MipsLabel(thenBB.getName())));
                        mipsBlock.setTrueSucc(thenBB.getMipsBlock());
                    } else{
                        mipsBlock.addInstruction(new MipsJ(new MipsLabel(elseBB.getName())));
                        mipsBlock.setTrueSucc(elseBB.getMipsBlock());
                    }
                } else{
                    MipsOperand condOp = cond.toMipsOperand(false, function, block);
                    mipsBlock.addInstruction(new MipsBeqz(condOp, new MipsLabel(elseBB.getName())));
                    mipsBlock.addInstruction(new MipsJ(new MipsLabel(thenBB.getName())));
                    mipsBlock.setTrueSucc(thenBB.getMipsBlock());
                    mipsBlock.setFalseSucc(elseBB.getMipsBlock());
                }
            }
        }
    }
}
