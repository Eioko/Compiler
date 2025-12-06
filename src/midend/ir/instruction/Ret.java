package midend.ir.instruction;

import backend.MipsModule;
import backend.component.MipsBlock;
import backend.instruction.*;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.type.DataType;
import midend.ir.type.VoidType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import java.util.HashSet;
import java.util.TreeSet;

import static backend.operand.MipsPhyReg.*;
import static utils.Configs.optimize;

public class Ret extends Instruction {
    public Ret(BasicBlock parent) {
        super("",new VoidType(), parent);
    }

    public Ret(int nameNum, BasicBlock parent, Value returnValue) {
        super("%v"+nameNum, (DataType) returnValue.getValueType(), parent, returnValue);
    }

    @Override
    public String toString() {
        if(this.getValueType() instanceof VoidType) {
            return "ret void";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ret ");
        sb.append(this.getValueType().toString());
        sb.append(" ");
        sb.append(this.getUsedValue(0).getName());
        return sb.toString();
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        if(!optimize){
            if(this.getValueType() instanceof VoidType) {
                mipsBlock.addInstruction(new MipsJr(RA));
            }else{
                if(function.getMipsFunction() == MipsModule.getInstance().mainFunction){
                    // exit syscall
                    mipsBlock.addInstruction(new MipsLi(V0, new MipsImm(10))); // syscall code 10 for exit
                    mipsBlock.addInstruction(new MipsSyscall());
                    return;
                }
                MipsOperand retVal = this.getUsedValue(0).toSimpleReg(false, function, block, 0);
                loadMemToReg(this.getUsedValue(0), retVal, block, function);
                mipsBlock.addInstruction(new MipsMove(V0, retVal));
                mipsBlock.addInstruction(new MipsJr(RA));
            }
            mipsBlock.addInstruction(new MipsEmpty());
        }
        else{
            if(! (this.getValueType() instanceof VoidType)) {
                MipsOperand retVal = this.getUsedValue(0).toMipsOperand(false, function, block);
                MipsMove mipsMove = new MipsMove(V0, retVal);
                mipsBlock.addInstruction(mipsMove);
            }
            MipsRet mipsRet = new MipsRet(function.getMipsFunction());
            mipsRet.addUseReg(null, V0);
            mipsBlock.addInstruction(mipsRet);
        }
    }
}
