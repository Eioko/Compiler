package backend.instruction;

import backend.operand.MipsOperand;

public class MipsLi extends MipsInstruction{
    private MipsOperand dest;
    private MipsOperand value;
    public MipsLi(MipsOperand dest, MipsOperand value) {
        this.dest = dest;
        this.value = value;
    }

    @Override
    public String toString() {
        return "li " + dest.toString() + ", " + value.toString();
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            dest = newReg;
        }
        if (value != null && value.equals(oldReg)) {
            value = newReg;
        }
        super.replaceReg(oldReg, newReg);
    }
}
