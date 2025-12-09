package midend.ir.instruction;

import backend.component.MipsBlock;
import backend.instruction.*;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import midend.ir.type.DataType;
import midend.ir.type.VoidType;
import midend.ir.value.Argument;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;

import java.util.ArrayList;

import static backend.MipsModule.*;
import static backend.operand.MipsPhyReg.*;
import static utils.Configs.regAlloca;

public class Call extends Instruction {
    /**
     * 有返回值调用
     * @param nameNum
     * @param function
     * @param parent
     * @param args
     */
    public Call(int nameNum, Function function, BasicBlock parent, ArrayList<Value> args) {
        super("%v" + nameNum, function.getReturnType(), parent, new ArrayList<Value>(){
                    {
                        add(function);
                        addAll(args);
                    }
                }.toArray(new Value[0]));
    }

    /**
     * 无返回值调用
     * @param function
     * @param parent
     */
    public Call(Function function, BasicBlock parent, ArrayList<Value> args) {
        super("" , function.getReturnType(), parent, new ArrayList<Value>(){
            {
                add(function);
                addAll(args);
            }
        }.toArray(new Value[0]));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!(this.getValueType() instanceof VoidType)) {
            sb.append(this.getName()).append(" = ");
        }
        sb.append("call ").append(this.getValueType().toString()).append(" ");
        sb.append(this.getUsedValue(0).getName()).append("(");
        for (int i = 1; i < this.getNumOfOperands(); i++) {
            sb.append(this.getUsedValue(i).getValueType().toString()).append(" ")
                    .append(this.getUsedValue(i).getName());
            if (i != this.getNumOfOperands() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public void toMips(BasicBlock block, Function function) {
        Function calleeFunction = (Function) this.getUsedValue(0);
        MipsBlock mipsBlock = block.getMipsBlock();

        if(!regAlloca){
            int currentOffset = getCurrentStackOffset();
            ArrayList<MipsPhyReg> allocatedRegisterList = getAllocatedRegs();

            ArrayList<Value> args = new ArrayList<>(this.getOperands().subList(1, this.getNumOfOperands()));

            int newlen = 4 * allocatedRegisterList.size() + 12 + args.size() * 4;

            mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.ADDU, SP , SP, new MipsImm(-newlen)));
            //调用者保存现场
            saveCurrent(block, currentOffset ,allocatedRegisterList);

            currentOffset -= 4 * allocatedRegisterList.size();
            currentOffset -= 12; //为FP、SP和RA腾出空间

            //填参
            for(int i = 0; i < args.size(); i++) {
                Value arg = args.get(i);
                if(i<4) {
                    //前4个参数放寄存器
                    doRealPara(arg, i, currentOffset, block, function,  allocatedRegisterList);
                }else{
                    MipsPhyReg argReg = doRealPara(arg, i, currentOffset ,block, function,  allocatedRegisterList);
                    mipsBlock.addInstruction(new MipsSw(argReg, new MipsImm(- 4 * i - 4 + currentOffset), FP));
                }
            }

            //跳转, 我的fp在函数参数之上，在sp、ra之下
            mipsBlock.addInstruction(new MipsBinary(MipsBinary.BinaryOp.ADDU, FP , FP, new MipsImm(currentOffset)));
            mipsBlock.addInstruction(new MipsJal(calleeFunction.getMipsFunction()));

            // 恢复SP寄存器和RA寄存器
            mipsBlock.addInstruction(new MipsLw(RA, new MipsImm(0), FP));
            mipsBlock.addInstruction(new MipsLw(SP, new MipsImm(4), FP));
            mipsBlock.addInstruction(new MipsLw(FP, new MipsImm(8), FP));

            // 恢复已分配的寄存器
            int registerNum = 0;
            for (MipsPhyReg register : allocatedRegisterList) {
                registerNum++;
                mipsBlock.addInstruction(new MipsLw(register, new MipsImm(-4 * registerNum), FP));
            }

            saveRegToStack(this, new MipsPhyReg(MipsPhyReg.Register.V0), block, function);
            mipsBlock.addInstruction(new MipsEmpty());
        }
        // 优化情况下
        else{
            ArrayList<Value> args = new ArrayList<>(this.getOperands().subList(1, this.getNumOfOperands()));
            MipsJal mipsJal = new MipsJal(calleeFunction.getMipsFunction());

            for(int i = 0; i < args.size(); i++) {
                Value arg = args.get(i);
                MipsOperand mipsSrc;
                if (i < 4) {
                    mipsSrc = arg.toMipsOperand(false, function, block);
                    MipsPhyReg srcReg = switch (i) {
                        case 0 -> MipsPhyReg.A0;
                        case 1 -> MipsPhyReg.A1;
                        case 2 -> MipsPhyReg.A2;
                        default -> MipsPhyReg.A3;
                    };
                    // 这里move会有覆盖吗？
                    MipsMove mipsMove = new MipsMove(srcReg, mipsSrc);
                    mipsBlock.addInstruction(mipsMove);
                    mipsJal.addUseReg(null, mipsMove.getDst());
                } else {
                    mipsSrc = arg.toMipsOperand(false, function, block);

                    int offset = -(args.size() - i) * 4;
                    MipsSw mipsStore = new MipsSw(mipsSrc, new MipsImm(offset), SP);
                    mipsBlock.addInstruction(mipsStore);
                }
            }
            if (args.size() > 4) {
                MipsOperand mipsOffset = parseConstIntOperand(4 * (args.size() - 4), true, function, block);
                MipsBinary mipsSub = new MipsBinary(MipsBinary.BinaryOp.SUBU ,SP, SP, mipsOffset);
                mipsBlock.addInstruction(mipsSub);
            }
            mipsBlock.addInstruction(mipsJal);

            if (args.size() > 4) {
                MipsOperand mipsOffset = parseConstIntOperand(4 * (args.size() - 4), true, function, block);
                MipsBinary mipsAdd = new MipsBinary(MipsBinary.BinaryOp.ADDU ,SP, SP, mipsOffset);
                mipsBlock.addInstruction(mipsAdd);
            }
            mipsJal.addDefReg(null, A0);
            mipsJal.addDefReg(null, A1);
            mipsJal.addDefReg(null, A2);
            mipsJal.addDefReg(null, A3);
            mipsJal.addDefReg(null, RA);
            DataType returnType = (DataType) this.getValueType();
            mipsJal.addDefReg(null, V0);
            if (!(returnType instanceof VoidType)) {
                MipsMove mipsMove = new MipsMove(this.toMipsOperand(false, function, block), V0);
                mipsBlock.addInstruction(mipsMove);
            }
        }
    }

    public MipsPhyReg doRealPara(Value arg, int i, int beforeOffset, BasicBlock block, Function function,
                           ArrayList<MipsPhyReg> allocatedRegisterList){
        MipsBlock mipsBlock = block.getMipsBlock();
        MipsPhyReg argReg = new MipsPhyReg(MipsPhyReg.Register.values()[MipsPhyReg.Register.A0.ordinal() + i]);
        if(arg instanceof Argument){
            MipsPhyReg reg = getValueToReg(arg, function);
            if(reg != null){
                //这里会不会RE？？？
                mipsBlock.addInstruction(new MipsLw(argReg,
                        new MipsImm(-4 * allocatedRegisterList.indexOf(reg) - 4 + beforeOffset), FP));
            }else{
                loadMemToReg(arg, argReg, block, function);
            }
        }else{
            loadMemToReg(arg, argReg, block, function);
        }
        return argReg;
    }
    public void saveCurrent(BasicBlock block, int beforeOffset,ArrayList<MipsPhyReg> allocatedRegisterList) {
        MipsBlock mipsBlock = block.getMipsBlock();
        // 获取已分配的寄存器列表
        int registerNum = 0;
        for (MipsPhyReg register : allocatedRegisterList) {
            registerNum++;
            mipsBlock.addInstruction(new MipsSw(register, new MipsImm(-registerNum * 4 + beforeOffset), FP));
        }
        // 保存SP、RA、FP
        mipsBlock.addInstruction(new MipsSw(FP, new MipsImm( -4 - registerNum * 4 + beforeOffset), FP));
        mipsBlock.addInstruction(new MipsSw(SP, new MipsImm( -8 - registerNum * 4 + beforeOffset), FP));
        mipsBlock.addInstruction(new MipsSw(RA, new MipsImm( -12 - registerNum * 4 + beforeOffset), FP));
    }
}



