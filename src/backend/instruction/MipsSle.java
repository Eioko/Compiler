package backend.instruction;

import backend.operand.MipsOperand;

public class MipsSle extends MipsInstruction {
    private final MipsOperand dest;
    private final MipsOperand src1;
    private final MipsOperand src2;

    public MipsSle(MipsOperand dest, MipsOperand src1, MipsOperand src2) {
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }

    @Override
    public String toString() {
        return "slt " + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
    }
}
