package backend.instruction;

import backend.operand.MipsLabel;

public class MipsJ extends MipsInstruction {
    private final MipsLabel target;

    public MipsJ(MipsLabel target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "j " + target.toString();
    }
}
