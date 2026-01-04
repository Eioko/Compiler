package backend.component;

import backend.instruction.MipsInstruction;
import backend.instruction.MipsJ;

import java.util.ArrayList;
import java.util.LinkedList;

public class MipsBlock{
    private static int index = 0;
    private String name;
    private LinkedList<MipsInstruction> instructions;

    private MipsBlock falseSucc = null;
    private MipsBlock trueSucc = null;

    private final ArrayList<MipsBlock> preds = new ArrayList<>();

    public MipsBlock(String name) {
        index++;
        this.name = name;
        this.instructions = new LinkedList<>();
    }
    public MipsBlock() {
        index++;
        this.name = "transfer_" + index;
        this.instructions = new LinkedList<>();
    }
    public String getName() {
        return name;
    }
    public void addInstruction(MipsInstruction instruction){
        /*if (this.name.equals("b78")) {
            System.out.println("DEBUG: Adding instruction to b78: " + instruction);
            // 打印调用栈，查看是谁调用的
            new Exception("Stack Trace").printStackTrace(System.out);
        }*/
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
            sb.append("\t").append(instruction.toString()).append("\n\n");
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
    public ArrayList<MipsBlock> getPreds() {
        return preds;
    }
    public void addPred(MipsBlock pred) {
        this.preds.add(pred);
    }

    public void removePred(MipsBlock pred) {
        this.preds.remove(pred);
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
    public void insertPhiCopysHead(ArrayList<MipsInstruction> phiMoves) {
        for (int i = phiMoves.size() - 1; i >= 0; i--) {
            instructions.addFirst(phiMoves.get(i));
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
    public void insertPhiMovesTail(ArrayList<MipsInstruction> phiMoves) {
        /*if(this.name.equals("b78")){
            System.out.println("debug");
            System.out.println(this);
            System.out.println("Inserting phi move at tail: " + phiMoves);
        }*/
        if (instructions.isEmpty()) {
            instructions.addAll(phiMoves);
            return;
        }

        MipsInstruction last = instructions.getLast();
        // 只有当最后一条指令是跳转指令时，才插在它前面
        if (last instanceof MipsJ ) {
            int index = instructions.size() - 1;
            for (MipsInstruction phiMove : phiMoves) {
                instructions.add(index, phiMove);
                index++; // 保持插入顺序
            }
        } else {
            instructions.addAll(phiMoves);
        }
    }
    public void removeTailInstr() {
        instructions.removeLast();
    }
    public MipsInstruction getLastInstruction() {
        return instructions.getLast();
    }
}
