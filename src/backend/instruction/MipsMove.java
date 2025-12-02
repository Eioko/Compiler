package backend.instruction;

import backend.operand.MipsOperand;

public class MipsMove extends MipsInstruction {
    private MipsOperand dest;
    private MipsOperand src;

    public MipsMove(MipsOperand dest, MipsOperand src) {
        this.dest = dest;
        this.src = src;
    }

    public MipsOperand getDst() {
        return dest;
    }
    public MipsOperand getSrc() {
        return src;
    }
    @Override
    public String toString() {
        return "move " + dest.toString() + ", " + src.toString();
    }
    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            dest = newReg;
        }
        if (src != null && src.equals(oldReg)) {
            src = newReg;
        }
        super.replaceReg(oldReg, newReg);
    }
}
