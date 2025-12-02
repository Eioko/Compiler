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

    public void setOffset(MipsOperand offset) {
        addUseReg(this.offset, offset);
        this.offset = offset;
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            dest = newReg;
        }
        if (offset != null && offset.equals(oldReg)) {
            offset = newReg;
        }
        if (base != null && base.equals(oldReg)) {
            base = newReg;
        }
        super.replaceReg(oldReg, newReg);
    }
}
