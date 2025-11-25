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
}
