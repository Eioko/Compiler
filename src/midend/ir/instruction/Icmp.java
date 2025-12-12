package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsSeq;
import backend.instruction.MipsSle;
import backend.instruction.MipsSlt;
import backend.instruction.MipsSne;
import backend.operand.MipsOperand;
import midend.ir.type.IntegerType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static utils.Configs.regAlloca;

public class Icmp extends Instruction {
    public enum IcmpOp {
        LT, GT, LE, GE, EQ, NE
    }

    private IcmpOp op;

    public Icmp(int nameNum, IcmpOp op, Value left, Value right, BasicBlock parent) {
        super("%v"+nameNum, new IntegerType(1), parent, left, right);
        this.op = op;
    }

    public IcmpOp getOp() {
        return op;
    }

    public Value getLeft() {
        return getUsedValue(0);
    }

    public Value getRight() {
        return getUsedValue(1);
    }

    @Override
    public String toString() {
        String opStr = switch (op) {
            case LT -> "icmp slt";
            case GT -> "icmp sgt";
            case LE -> "icmp sle";
            case GE -> "icmp sge";
            case EQ -> "icmp eq";
            case NE -> "icmp ne";
        };
        return  getName() + " = " + opStr + " " +
                getLeft().getValueType().toString() + " " + getLeft().getName() + ", " +
                getRight().getName();
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        if(!regAlloca){
            MipsOperand leftOp = getLeft().toSimpleReg(false, function, block, 0);
            MipsOperand rightOp = getRight().toSimpleReg(false, function, block, 1);
            MipsOperand dest = this.toSimpleReg(false, function, block, 2);

            loadMemToReg(getLeft(), leftOp, block, function);
            loadMemToReg(getRight(), rightOp, block, function);

            if(op == IcmpOp.EQ){
                mipsBlock.addInstruction(new MipsSeq(dest, leftOp, rightOp));
            } else if(op == IcmpOp.NE){
                mipsBlock.addInstruction(new MipsSne(dest, leftOp, rightOp));
            } else if(op == IcmpOp.LT){
                mipsBlock.addInstruction(new MipsSlt(dest, leftOp, rightOp));
            } else if(op == IcmpOp.LE){
                mipsBlock.addInstruction(new MipsSle(dest, leftOp, rightOp));
            } else if(op == IcmpOp.GT){
                mipsBlock.addInstruction(new MipsSlt(dest, rightOp, leftOp));
            } else if(op == IcmpOp.GE){
                mipsBlock.addInstruction(new MipsSle(dest, rightOp, leftOp));
            }
            saveRegToStack(this, dest, block, function);
            mipsBlock.addInstruction(new backend.instruction.MipsEmpty());
        }
        else{
            MipsOperand leftOp = getLeft().toMipsOperand(false, function, block);
            MipsOperand rightOp = getRight().toMipsOperand(false, function, block);
            MipsOperand dest = this.toMipsOperand(false, function, block);

            if(op == IcmpOp.EQ){
                mipsBlock.addInstruction(new MipsSeq(dest, leftOp, rightOp));
            } else if(op == IcmpOp.NE){
                mipsBlock.addInstruction(new MipsSne(dest, leftOp, rightOp));
            } else if(op == IcmpOp.LT){
                mipsBlock.addInstruction(new MipsSlt(dest, leftOp, rightOp));
            } else if(op == IcmpOp.LE){
                mipsBlock.addInstruction(new MipsSle(dest, leftOp, rightOp));
            } else if(op == IcmpOp.GT){
                mipsBlock.addInstruction(new MipsSlt(dest, rightOp, leftOp));
            } else if(op == IcmpOp.GE){
                mipsBlock.addInstruction(new MipsSle(dest, rightOp, leftOp));
            }
            mipsBlock.addInstruction(new backend.instruction.MipsEmpty());
        }
    }
}
