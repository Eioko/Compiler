package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.MipsBinary;
import backend.instruction.MipsEmpty;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.constant.ConstArray;
import midend.ir.constant.Constant;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;

import static backend.MipsModule.allocateStackSpace;
import static backend.MipsModule.getCurrentStackOffset;
import static backend.operand.MipsPhyReg.FP;
import static backend.operand.MipsPhyReg.SP;
import static utils.Configs.optimize;

public class  Alloca extends Instruction{
    // 局部常量数组初始化值
    private ConstArray initVal = null;
    public Alloca(int numNum, ValueType allocatedType, BasicBlock parent) {
        super("%p"+numNum, new PointerType(allocatedType), parent);
    }
    public Alloca(int nameNum, ValueType allocatedType, BasicBlock parent, ConstArray initVal) {
        // 指针
        super("%p" + nameNum, new PointerType(allocatedType), parent);
        this.initVal = initVal;
    }
    public Alloca(int nameNum, ValueType allocatedType, BasicBlock parent, Constant initVal) {
        // 指针
        super("%p" + nameNum, new PointerType(allocatedType), parent);
        this.initVal = null;
        //??这里还没有实现
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = alloca ");
        sb.append(((PointerType)this.getValueType()).getPointeeType().toString());
        return sb.toString();
    }
    public ConstArray getInitVal(){
        return initVal;
    }

    public void toMips(BasicBlock block, Function function) {
        MipsBlock mipsBlock = block.getMipsBlock();
        MipsFunction mipsFunction = function.getMipsFunction();

        int size = (((PointerType)this.getValueType()).getPointeeType()).getSizeInBytes();
        mipsFunction.addAllocaSize(size);
        if(!optimize){
            //单纯分配空间，减少offset
            allocateStackSpace(size);
            MipsOperand destAddr = this.toSimpleReg(true , function, block, 2);
            //数据在的位置
            MipsOperand realOffset = new MipsImm(getCurrentStackOffset());
            mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.ADDU, destAddr, FP, realOffset));
            //把指针值存在再下面
            saveRegToStack(this, destAddr ,block, function);
            mipsBlock.addInstruction(new MipsEmpty());
        }
        else{
            MipsOperand offset = parseConstIntOperand(mipsFunction.getAllocaSize(), true, function, block);
            MipsOperand dst = this.toMipsOperand(true, function, block);
            MipsBinary mipsAdd = new MipsBinary(MipsBinary.BinaryOp.ADDU, dst,  SP, offset);
            mipsBlock.addInstruction(mipsAdd);
            mipsBlock.addInstruction(new MipsEmpty());
        }
    }
}

