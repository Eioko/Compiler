package backend.instruction;

import backend.operand.MipsOperand;

public class MipsLw extends MipsInstruction {
    private MipsOperand dest;
    private MipsOperand offset;
    private MipsOperand base;

    public MipsLw(MipsOperand dest, MipsOperand offset, MipsOperand base) {
        this.dest = dest;
        this.offset = offset;
        this.base = base;
    }

    public String toString() {
        return "lw " + dest.toString() + ", " + offset.toString() + "(" + base.toString() + ")";
    }
}
