package backend.instruction;

import backend.operand.MipsOperand;

public class MipsJr extends MipsInstruction {
    private MipsOperand target;

    public MipsJr(MipsOperand target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "jr " + target.toString();
    }
}
