package midend.ir.value;

import midend.ir.instruction.Instruction;
import midend.ir.type.LabelType;

import java.util.LinkedList;

public class  BasicBlock extends Value{
    private final LinkedList<Instruction> instList = new LinkedList<>();
    public BasicBlock(int num , Function function) {
        super("%b"+num, new LabelType(), function);
    }
    public Function getParent(){
        return (Function) super.getParent();
    }
    public void insertTail(Instruction inst){
        for(Instruction instruction : instList){
            if(instruction == inst){
                throw new AssertionError("Instruction already in BasicBlock");
            }
        }
        instList.add(inst);
    }
    public void insertHead(Instruction inst){
        for(Instruction instruction : instList){
            if(instruction == inst){
                throw new AssertionError("Instruction already in BasicBlock");
            }
        }
        instList.addFirst(inst);
    }

    public Instruction getLastInst(){
        if(instList.isEmpty()){
            return null;
        }
        return instList.getLast();
    }

    // Print label and all instructions with indentation
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(":\n");
        for (Instruction instruction : instList) {
            sb.append("  ").append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

}
