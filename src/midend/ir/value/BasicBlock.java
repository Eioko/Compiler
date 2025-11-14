package midend.ir.value;

import midend.ir.instruction.Instruction;
import midend.ir.type.LabelType;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicBlock extends Value{
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

}
