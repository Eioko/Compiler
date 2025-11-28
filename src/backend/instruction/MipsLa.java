package backend.instruction;

import backend.operand.MipsOperand;

public class MipsLa extends MipsInstruction {
    private MipsOperand addr;
    private MipsOperand dest;

    public MipsLa(MipsOperand dest, MipsOperand addr) {
        this.dest = dest;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "la " + dest.toString() + ", " + addr;
    }
}
