package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.MipsBinary;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import midend.ir.type.ArrayType;
import midend.ir.type.IntegerType;
import midend.ir.type.PointerType;
import midend.ir.type.ValueType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

public class GEP extends Instruction {
    private final ValueType baseType;

    /**
     * 函数参数数组寻址, 或者说是base为指针的寻址
     */
    public GEP(int nameNum, BasicBlock parent, Value base, Value firstIndex) {
        super("%p" + nameNum, (PointerType) base.getValueType(), parent, base, firstIndex);
        this.baseType = ((PointerType) base.getValueType()).getPointeeType();
    }
    /**
    正常寻址，一维数组，firstIndex为0，secondIndex为具体索引
     例如：数组a[10]，寻址a[3]，firstIndex=0，secondIndex=3
     */
    public GEP(int nameNum, BasicBlock parent, Value base, Value firstIndex, Value secondIndex) {
        super("%p" + nameNum, new PointerType(new IntegerType()), parent, base, firstIndex, secondIndex);
        this.baseType = ((PointerType) base.getValueType()).getPointeeType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = getelementptr inbounds ");
        sb.append(baseType.toString());
        for (int i = 0; i < this.getNumOfOperands(); i++) {
            sb.append(", ");
            sb.append(this.getUsedValue(i).getValueType().toString());
            sb.append(" ");
            sb.append(this.getUsedValue(i).getName());
        }
        return sb.toString();
    }

    public void toMips(BasicBlock block, Function function){
        MipsBlock mipsBlock = block.getMipsBlock();
        Value baseValue = this.getUsedValue(0);
        MipsOperand base = baseValue.toMipsOperand(false, function, block, 0);
        MipsOperand dest = this.toMipsOperand(false, function, block, 2);
        loadMemToReg(baseValue, base, block, function);
        Value indexValue;
        if(getNumOfOperands() == 2){
            indexValue = this.getUsedValue(1);
        }else{
            //其实我的二维寻址好像都是0，0，这里无所谓
            indexValue = this.getUsedValue(2);
        }
        MipsOperand index = indexValue.toMipsOperand(false, function, block, 1);
        loadMemToReg(indexValue, index, block, function);
        MipsImm imm = new MipsImm(2);
        mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.SLL, index, index, imm));
        mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.ADDU, dest, base, index));
        saveRegToStack(this, dest ,block, function);
    }
}
