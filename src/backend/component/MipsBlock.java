package backend.component;

import backend.instruction.MipsInstruction;

import java.util.ArrayList;

public class MipsBlock{
    private static int index = 0;
    private String name;
    private ArrayList<MipsInstruction> instructions;

    public MipsBlock(String name) {
        index++;
        this.name = name;
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(MipsInstruction instruction){
        instructions.add(instruction);
    }

}
