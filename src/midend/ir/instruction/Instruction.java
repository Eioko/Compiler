package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.*;
import backend.operand.MipsImm;
import backend.operand.MipsLabel;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.constant.ConstInt;
import midend.ir.type.DataType;
import midend.ir.value.*;

import static backend.MipsModule.*;
import static backend.operand.MipsPhyReg.FP;

public class Instruction extends User {
    /**
     * @param name
     * @param dataType 指令的返回值类型
     * @param parent
     * @param ops
     */
    public Instruction(String name, DataType dataType, BasicBlock parent, Value... ops) {
        super(name, dataType, parent, ops);
    }

    public void toMips(BasicBlock block, Function function) {}

    public void saveRegToStack(Value value, MipsOperand src, BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();

        int offset = allocateStackForValue(mipsBlock, value);
        mipsBlock.addInstruction(new MipsSw(src, new MipsImm(offset), FP));
    }

    public static void loadMemToReg(Value val, MipsOperand dest, BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();

        if(val instanceof ConstInt){
            int num = ((ConstInt)val).getNumber();
            MipsImm imm = new MipsImm(num);
            mipsBlock.addInstruction(new MipsLi(dest, imm));
        }else if(val instanceof GlobalVariable){
            MipsLabel mipsLabel = new MipsLabel(val.getName());
            mipsBlock.addInstruction(new MipsLa(dest, mipsLabel));
        }else{
            MipsPhyReg reg = getValueToReg(val, function);
            if(reg != null){
                mipsBlock.addInstruction(new MipsMove(dest, reg));
                return;
            }

            Integer offset = getValStackOffset(val);
            if(offset == null){
                offset = allocateStackForValue(mipsBlock, val);
            }
            mipsBlock.addInstruction(new MipsLw(dest,new MipsImm(offset), FP));
        }
    }
}
