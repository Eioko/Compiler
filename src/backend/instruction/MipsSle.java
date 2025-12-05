package backend.instruction;

import backend.operand.MipsOperand;

public class MipsSle extends MipsInstruction {
    private MipsOperand dest;
    private MipsOperand src1;
    private MipsOperand src2;

    public MipsSle(MipsOperand dest, MipsOperand src1, MipsOperand src2) {
        setDst(dest);
        setSrc1(src1);
        setSrc2(src2);
    }

    public void setDst(MipsOperand dst) {
        addDefReg(this.dest, dst);
        this.dest = dst;
    }
    public void setSrc1(MipsOperand src1) {
        addUseReg(this.src1, src1);
        this.src1 = src1;
    }

    public void setSrc2(MipsOperand src2) {
        addUseReg(this.src2, src2);
        this.src2 = src2;
    }
    @Override
    public String toString() {
        return "sle " + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
    }

    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest!=null && dest.equals(oldReg)) {
            dest = newReg;
        }
        if (src1!=null && src1.equals(oldReg)) {
            src1 = newReg;
        }
        if (src2!=null && src2.equals(oldReg)) {
            src2 = newReg;
        }
    }
}
