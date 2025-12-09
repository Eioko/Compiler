package backend.component;

import backend.MipsModule;
import backend.instruction.MipsInstruction;
import backend.operand.MipsImm;
import backend.operand.MipsPhyReg;
import backend.operand.MipsReg;
import backend.operand.MipsVirReg;

import java.util.HashSet;
import java.util.LinkedList;

import static utils.Configs.regAlloca;

public class MipsFunction {
    private String name;

    public LinkedList<MipsBlock> blocks = new LinkedList<>();
    private final HashSet<MipsVirReg> usedVirRegs = new HashSet<>();

    public MipsFunction(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void addBlock(MipsBlock block) {
        blocks.add(block);
    }
    public LinkedList<MipsBlock> getBlocks() {
        return blocks;
    }

    /**
     * 优化用，用callee保存现场，因为要重新计算栈大小
     */
    private int totalStackSize = 0;
    private int allocaSize = 0;
    public void addAllocaSize(int size) {
        allocaSize += size;
    }
    public int getAllocaSize() {
        return allocaSize;
    }
    public int getTotalStackSize() {
        return totalStackSize;
    }
    // 用于调整内存空间的参数偏移
    private final HashSet<MipsImm> argOffsets = new HashSet<>();
    private final HashSet<Integer> calleeSavedRegIndexes = new HashSet<>();
    public void addArgOffset(MipsImm mipsOffset) {
        argOffsets.add(mipsOffset);
    }
    public void addUsedVirReg(MipsVirReg mipsVirReg) {
        usedVirRegs.add(mipsVirReg);
    }
    public HashSet<MipsVirReg> getUsedVirRegs() {
        return usedVirRegs;
    }
    public HashSet<Integer> getCalleeSavedRegIndexes() {
        return calleeSavedRegIndexes;
    }

    public void fixStack() {
        for (MipsBlock mipsBlock : blocks) {
            for (MipsInstruction instr : mipsBlock.getInstructions()) {
                for (MipsReg defReg : instr.getDefRegs()) {
                    if(!(defReg instanceof MipsPhyReg)) continue;
                    int index = ((MipsPhyReg) defReg).getIndex();
                    if (MipsPhyReg.calleeSavedRegIndex.contains(index)) {
                        calleeSavedRegIndexes.add(index);
                    }
                }
            }
        }
        int stackRegSize = 4 * calleeSavedRegIndexes.size();

        totalStackSize = stackRegSize + allocaSize;

        for (MipsImm argOffset : argOffsets) {
            int newOffset = argOffset.getNumber() + totalStackSize;
            argOffset.setNum(newOffset);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":").append("\n");
        if(this == MipsModule.getInstance().mainFunction){
            sb.append("\tmove $fp, $sp\n");
        }else{
            if(regAlloca){
                int stackOffset = -4;
                for (Integer savedRegIndex : calleeSavedRegIndexes)
                {
                    sb.append("\t").append("sw ").append(MipsPhyReg.getReg(savedRegIndex)).append(",\t")
                            .append(stackOffset).append("($sp)\n");
                    stackOffset -= 4;
                }
            }
        }
        if(regAlloca){
            if (totalStackSize != 0) {
                sb.append("\tadd $sp,\t$sp,\t").append(-totalStackSize).append("\n");
            }
        }

        for(MipsBlock block : blocks){
            sb.append(block.toString()).append("\n");
        }
        return sb.toString();
    }
}
