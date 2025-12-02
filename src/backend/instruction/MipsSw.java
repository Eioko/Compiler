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

    public void setOffset(MipsOperand offset) {
        addUseReg(this.offset, offset);
        this.offset = offset;
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (src != null && src.equals(oldReg)) {
            src = newReg;
        }
        if (offset != null && offset.equals(oldReg)) {
            offset = newReg;
        }
        if (dest != null && dest.equals(oldReg)) {
            dest = newReg;
        }
        super.replaceReg(oldReg, newReg);
    }
}
