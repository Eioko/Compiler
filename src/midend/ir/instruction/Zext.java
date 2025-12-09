package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsMove;
import backend.operand.MipsOperand;
import midend.ir.type.DataType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import static utils.Configs.regAlloca;

public class Zext extends Instruction {
    private Value from;
    private DataType toType;
    public Zext(int nameNum, Value from, DataType toType, BasicBlock parent) {
        super("%v"+nameNum, toType, parent, from);
        this.from = from;
        this.toType = toType;
    }
    public Value getFrom() {
        return from;
    }
    public DataType getToType() {
        return toType;
    }
    @Override
    public String toString() {
        return getName() + " = zext " +
                from.getValueType().toString() + " " + from.getName() +
                " to " + toType.toString();
    }
    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        if(!regAlloca){
            MipsOperand dest = this.toSimpleReg(false, function, block, 2);
            loadMemToReg(from, dest, block, function);
            saveRegToStack(this, dest, block, function);
        }else{
            MipsOperand dest = this.toMipsOperand(false, function, block);
            MipsOperand src = from.toMipsOperand(false, function, block);
            mipsBlock.addInstruction(new MipsMove(dest, src));
        }

        mipsBlock.addInstruction(new MipsEmpty());
    }
}

