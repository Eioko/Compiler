package backend.instruction;

import backend.operand.MipsOperand;

public class MipsSne extends MipsInstruction {
    private backend.operand.MipsOperand dest;
    private backend.operand.MipsOperand src1;
    private backend.operand.MipsOperand src2;

    public MipsSne(backend.operand.MipsOperand dest, backend.operand.MipsOperand src1, backend.operand.MipsOperand src2) {
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
        return "sne " + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
    }
    public void replaceReg(backend.operand.MipsOperand oldReg, backend.operand.MipsOperand newReg) {
        if (dest!=null && dest.equals(oldReg)) {
            setDst(newReg);
        }
        if (src1!= null && src1.equals(oldReg)) {
            setSrc1(newReg);
        }
        if (src2!= null && src2.equals(oldReg)) {
            setSrc2(newReg);
        }
    }
}
