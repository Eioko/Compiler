package midend.ir.value;

import backend.component.MipsBlock;
import midend.ir.instruction.Instruction;
import midend.ir.type.LabelType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class  BasicBlock extends Value{
    private final LinkedList<Instruction> instList = new LinkedList<>();
    public BasicBlock(int num , Function function) {
        super("b"+num, new LabelType(), function);
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
        sb.append("\n").append(getName()).append(":\n");
        for (Instruction instruction : instList) {
            sb.append("  ").append(instruction.toString()).append("\n");
        }
        return sb.toString();
    }

    private MipsBlock mipsBlock = null;
    public MipsBlock getMipsBlock() {
        return mipsBlock;
    }
    public void setMipsBlock(MipsBlock mipsBlock) {
        this.mipsBlock = mipsBlock;
    }
    public void toMips(Function function) {
        for (Instruction instruction : instList) {
            instruction.toMips(this, function);
        }
    }
    //-----------------------------------Mem2Reg---------------------------------------

    private final HashSet<BasicBlock> predecessors = new HashSet<>();
    private final HashSet<BasicBlock> successors = new HashSet<>();
    private final ArrayList<BasicBlock> domers = new ArrayList<>();
    private final ArrayList<BasicBlock> idomees = new ArrayList<>();
    private BasicBlock Idomer;
    private int domLevel;
    private final HashSet<BasicBlock> dominanceFrontier = new HashSet<>();

    public HashSet<BasicBlock> getPredecessors() {
        return predecessors;
    }
    public HashSet<BasicBlock> getSuccessors() {
        return successors;
    }
    public ArrayList<BasicBlock> getDomers() {
        return domers;
    }
    public ArrayList<BasicBlock> getIdomees() {
        return idomees;
    }
    public BasicBlock getIdomer() {
        return Idomer;
    }
    public void setIdomer(BasicBlock idomer) {
        Idomer = idomer;
    }
    public int getDomLevel() {
        return domLevel;
    }
    public void setDomLevel(int domLevel) {
        this.domLevel = domLevel;
    }
    public HashSet<BasicBlock> getDominanceFrontier() {
        return dominanceFrontier;
    }
    public void addPredecessor(BasicBlock pred) {
        predecessors.add(pred);
    }
    public void addSuccessor(BasicBlock succ) {
        successors.add(succ);
    }
    public LinkedList<Instruction> getInstList() {
        return instList;
    }
    public void eraseInstruction(Instruction instruction) {
        instList.remove(instruction);
    }
    public boolean isDominate(BasicBlock other) {
        return other.domers.contains(this);
    }
}

