package backend.instruction;

import backend.operand.MipsOperand;

public class MipsSw extends MipsInstruction {
    private MipsOperand src;
    private MipsOperand offset;
    private MipsOperand dest;

    public MipsSw(MipsOperand src, MipsOperand offset, MipsOperand dest) {
        setDst(dest);
        setAddr(src);
        setOffset(offset);
    }
    public void setDst(MipsOperand dst) {
        addUseReg(this.dest, dst);
        this.dest = dst;
    }

    public void setAddr(MipsOperand addr) {
        addUseReg(this.src, addr);
        this.src = addr;
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
            setAddr(newReg);
        }
        if (offset != null && offset.equals(oldReg)) {
            setOffset(newReg);
        }
        if (dest != null && dest.equals(oldReg)) {
            setDst(newReg);
        }
    }
}
