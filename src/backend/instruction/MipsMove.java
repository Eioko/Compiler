package backend.instruction;

import backend.operand.MipsOperand;

public class MipsMove extends MipsInstruction {
    private MipsOperand dest;
    private MipsOperand src;

    public MipsMove(MipsOperand dest, MipsOperand src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return "move " + dest.toString() + ", " + src.toString();
    }
}
