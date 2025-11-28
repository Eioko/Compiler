package backend.instruction;

import backend.operand.MipsOperand;

public class MipsSw extends MipsInstruction {
    private MipsOperand src;
    private MipsOperand offset;
    private MipsOperand dest;

    public MipsSw(MipsOperand src, MipsOperand offset, MipsOperand dest) {
        this.src = src;
        this.offset = offset;
        this.dest = dest;
    }
    public String toString() {
        return "sw " + src.toString() + ", " + offset.toString() + "(" + dest.toString() + ")";
    }
}
