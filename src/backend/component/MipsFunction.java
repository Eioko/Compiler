package backend.component;

import backend.MipsModule;

import java.util.ArrayList;

public class MipsFunction {
    private String name;
    private int stackSize = 0;
    private int allocaSize = 0;

    public ArrayList<MipsBlock> blocks = new ArrayList<>();

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
    public ArrayList<MipsBlock> getBlocks() {
        return blocks;
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
