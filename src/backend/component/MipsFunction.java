package backend.component;

import backend.MipsModule;
import backend.operand.MipsVirReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class MipsFunction {
    private String name;
    private int stackSize = 0;
    private int allocaSize = 0;

    public LinkedList<MipsBlock> blocks = new LinkedList<>();
    private final HashSet<MipsVirReg> usedVirRegs = new HashSet<>();

    public MipsFunction(String name) {
        this.name = name;
    }
    public void addAllocaSize(int size) {
        allocaSize += size;
    }
    public int getAllocaSize() {
        return allocaSize;
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

    public void addUsedVirReg(MipsVirReg objVirReg) {
        usedVirRegs.add(objVirReg);
    }
    public HashSet<MipsVirReg> getUsedVirRegs() {
        return usedVirRegs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":").append("\n");
        if(this == MipsModule.getInstance().mainFunction){
            sb.append("\tmove $fp, $sp\n");
        }

        for(MipsBlock block : blocks){
            sb.append(block.toString()).append("\n");
        }
        return sb.toString();
    }
}
