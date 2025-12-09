package backend.component;

import backend.instruction.MipsInstruction;

import java.util.ArrayList;
import java.util.LinkedList;

public class MipsBlock{
    private static int index = 0;
    private String name;
    private LinkedList<MipsInstruction> instructions;

    private MipsBlock falseSucc = null;
    private MipsBlock trueSucc = null;

    public MipsBlock(String name) {
        index++;
        this.name = name;
        this.instructions = new LinkedList<>();
    }
    public String getName() {
        return name;
    }
    public void addInstruction(MipsInstruction instruction){
        instructions.add(instruction);
    }
    public void addInstrHead(MipsInstruction instruction){
        instructions.addFirst(instruction);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        for(MipsInstruction instruction : instructions){
            sb.append("\t").append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

    public LinkedList<MipsInstruction> getInstructions() {
        return instructions;
    }

    public void setFalseSucc(MipsBlock falseSucc) {
        this.falseSucc = falseSucc;
    }

    public void setTrueSucc(MipsBlock trueSucc) {
        this.trueSucc = trueSucc;
    }

    public MipsBlock getFalseSucc() {
        return falseSucc;
    }

    public MipsBlock getTrueSucc() {
        return trueSucc;
    }
    public ArrayList<MipsBlock> getSuccessors(){
        ArrayList<MipsBlock> successors = new ArrayList<>();
        if(trueSucc != null){
            successors.add(trueSucc);
        }
        if(falseSucc != null){
            successors.add(falseSucc);
        }
        return successors;
    }
    public void insertBefore(MipsInstruction before, MipsInstruction instruction){
        for(MipsInstruction t : instructions){
            if(t.equals(before) ){
                int index = instructions.indexOf(t);
                instructions.add(index, instruction);
                return;
            }
        }
    }

    public void insertAfter(MipsInstruction after, MipsInstruction instruction){
        for(MipsInstruction t : instructions){
            if(t.equals(after) ){
                int index = instructions.indexOf(t);
                instructions.add(index + 1, instruction);
                return;
            }
        }
    }
}
