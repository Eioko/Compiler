package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsEmpty;
import backend.instruction.MipsLi;
import backend.instruction.MipsMove;
import backend.instruction.MipsSyscall;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.type.IntegerType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;

import static backend.operand.MipsPhyReg.V0;
import static utils.Configs.optimize;

public class GetInt extends Instruction{
    public GetInt(int nameNum, BasicBlock parent) {
        super("%v"+ nameNum, new IntegerType(), parent);
    }

    public String toString() {
        return this.getName() + " = call i32 @getint()";
    }

    public void toMips(BasicBlock bb, Function function) {
        MipsBlock mipsBlock = bb.getMipsBlock();

        MipsImm imm = new MipsImm(5); // syscall code for read integer
        mipsBlock.addInstruction(new MipsLi(V0, imm));
        mipsBlock.addInstruction(new MipsSyscall());

        if(!optimize){
            saveRegToStack(this, V0, bb, function);
        }else{
            MipsOperand dest = this.toMipsOperand(false, function, bb);
            //这里会有错误吗？
            mipsBlock.addInstruction(new MipsMove(dest, V0));
        }
        mipsBlock.addInstruction(new MipsEmpty());
    }
}
