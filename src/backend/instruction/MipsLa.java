package backend.instruction;

import backend.operand.MipsOperand;

public class MipsLa extends MipsInstruction {
    private MipsOperand addr;
    private MipsOperand dest;

    public MipsLa(MipsOperand dest, MipsOperand addr) {
        setDst(dest);
        setSrc(addr);
    }

    public void setDst(MipsOperand dst) {
        addDefReg(this.dest, dst);
        this.dest = dst;
    }

    public void setSrc(MipsOperand src) {
        addUseReg(this.addr, src);
        this.addr = src;
    }
    @Override
    public String toString() {
        return "la " + dest.toString() + ", " + addr;
    }

    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest!= null && dest.equals(oldReg)) {
            setDst(newReg);
        }
        if (addr!=null && addr.equals(oldReg)) {
            setSrc(newReg);
        }
    }
    @Override
    public void replaceUseReg(MipsOperand oldReg, MipsOperand newReg) {
        if (addr!=null && addr.equals(oldReg)) {
            setSrc(newReg);
        }
    }
}
