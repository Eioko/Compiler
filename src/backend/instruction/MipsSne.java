package backend.instruction;

public class MipsSne extends MipsInstruction {
    private final backend.operand.MipsOperand dest;
    private final backend.operand.MipsOperand src1;
    private final backend.operand.MipsOperand src2;

    public MipsSne(backend.operand.MipsOperand dest, backend.operand.MipsOperand src1, backend.operand.MipsOperand src2) {
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }

    @Override
    public String toString() {
        return "sne " + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
    }
}
