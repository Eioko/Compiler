package backend.instruction;

import backend.operand.MipsOperand;

public class MipsLi extends MipsInstruction{
    private MipsOperand dest;
    private MipsOperand value;
    public MipsLi(MipsOperand dest, MipsOperand value) {
        setDst(dest);
        setSrc(value);
    }

    public void setDst(MipsOperand dst) {
        addDefReg(this.dest, dst);
        this.dest = dst;
    }

    public void setSrc(MipsOperand src) {
        addUseReg(this.value, src);
        this.value = src;
    }
    @Override
    public String toString() {
        return "li " + dest.toString() + ", " + value.toString();
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            setDst(newReg);
        }
        if (value != null && value.equals(oldReg)) {
            setSrc(oldReg);
        }
    }
}
