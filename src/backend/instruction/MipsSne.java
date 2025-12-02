package backend.instruction;

public class MipsSne extends MipsInstruction {
    private backend.operand.MipsOperand dest;
    private backend.operand.MipsOperand src1;
    private backend.operand.MipsOperand src2;

    public MipsSne(backend.operand.MipsOperand dest, backend.operand.MipsOperand src1, backend.operand.MipsOperand src2) {
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }

    @Override
    public String toString() {
        return "sne " + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
    }
    public void replaceReg(backend.operand.MipsOperand oldReg, backend.operand.MipsOperand newReg) {
        if (dest!=null && dest.equals(oldReg)) {
            dest = newReg;
        }
        if (src1!= null && src1.equals(oldReg)) {
            src1 = newReg;
        }
        if (src2!= null && src2.equals(oldReg)) {
            src2 = newReg;
        }
    }
}
