package midend.ir.instruction;
import backend.MipsModule;
import backend.component.MipsBlock;
import backend.component.MipsFunction;
import backend.instruction.MipsInstruction;
import backend.instruction.MipsMove;
import backend.operand.MipsImm;
import backend.operand.MipsOperand;
import backend.operand.MipsVirReg;
import midend.ir.constant.ConstInt;
import midend.ir.type.DataType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Function;
import midend.ir.value.Value;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashSet;



public class Phi extends Instruction {
    private int predecessorNum;

    public Phi(int nameNum, DataType dataType, BasicBlock parent, int predecessorNum) {
        super("%p" + nameNum, dataType, parent, new Value[predecessorNum * 2]);
        this.predecessorNum = predecessorNum;
    }
    public void addIncoming(Value value, BasicBlock block) {
        int i = 0;
        while (i < predecessorNum && getUsedValue(i) != null) {
            i++;
        }
        if (i < predecessorNum) {
            setUsedValue(i, value);
            setUsedValue(i + predecessorNum, block);
        } else {
            getUsedValues().add(predecessorNum, value);
            predecessorNum++;
            getUsedValues().add(block);
        }
        value.addUser(this);
        block.addUser(this);
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getName() + " = phi ").append(getValueType());
        for (int i = 0; i < predecessorNum; i++) {
            if (getUsedValue(i) == null) break;
            s.append(" [ ").append(getUsedValue(i).getName()).append(", ").append("%")
                    .append(getUsedValue(i + predecessorNum).getName()).append(" ], ");
        }
        s.delete(s.length() - 2, s.length());
        return s.toString();
    }

    public static void doMips(Function function) {
        for(BasicBlock block: function.getBlocks()){
            HashSet<BasicBlock> predecessors = block.getPredecessors();
            int num = predecessors.size();
            if(num <= 1) {
                continue;
            }
            ArrayList<Phi> phis = new ArrayList<>();
            for(Instruction instr: block.getInstList()){
                if(instr instanceof Phi){
                    phis.add((Phi) instr);
                }else{
                    break;
                }
            }
            for(BasicBlock pred : predecessors){
                Pair<MipsBlock, MipsBlock> splitBlocks = new Pair<>(pred.getMipsBlock(), block.getMipsBlock());
                MipsModule.getInstance().phiCopysList.put(splitBlocks, genPhiCopys(phis, pred, function, block));
            }
        }
    }

    public Value getInputValForBlock(BasicBlock block) {
        for (int i = 0; i < predecessorNum; i++) {
            if (getUsedValue(i + predecessorNum) == block) {
                return getUsedValue(i);
            }
        }
        throw new AssertionError("block not found for phi!");
    }

    public void removeIncoming(BasicBlock block) {
        for (int i = 0; i < predecessorNum; i++) {
            if (getUsedValue(i + predecessorNum) == block) {
                Value val = getUsedValue(i);
                val.dropUser(this);
                block.dropUser(this);

                getUsedValues().remove(i + predecessorNum);
                getUsedValues().remove(i);

                predecessorNum--;
                return;
            }
        }
    }

    public static ArrayList<MipsInstruction> genPhiCopys(ArrayList<Phi> phis, BasicBlock pred, Function function, BasicBlock block){
        ArrayList<MipsInstruction> copyInstrs = new ArrayList<>();
        MipsFunction mipsFunction = function.getMipsFunction();
        ArrayList<Pair<MipsOperand, MipsOperand>> writes = new ArrayList<>();

        for(Phi phi: phis){
            MipsOperand phiDest = phi.toMipsOperand(false, function, block);
            Value inputValue = phi.getInputValForBlock(pred);
            MipsOperand phiSrc;
            if (inputValue instanceof ConstInt) {
                phiSrc = new MipsImm(((ConstInt) inputValue).getNumber());
            }
            else {
                phiSrc = inputValue.toMipsOperand(true, function, block);
            }

            if (phiDest.equals(phiSrc)) {
                continue;
            }

            // 使用临时寄存器解决并行赋值问题
            MipsVirReg temp = new MipsVirReg();
            mipsFunction.addUsedVirReg(temp);

            copyInstrs.add(new MipsMove(temp, phiSrc));
            writes.add(new Pair<>(phiDest, temp));
        }

        for (Pair<MipsOperand, MipsOperand> pair : writes) {
            copyInstrs.add(new MipsMove(pair.getFirst(), pair.getSecond()));
        }
        return copyInstrs;
    }

    public void toMips(){
        throw new AssertionError("Phi toMips should not be called!");
    }
}
