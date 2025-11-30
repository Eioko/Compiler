package backend.instruction;

import backend.operand.MipsLabel;
import backend.operand.MipsPhyReg;

public class MipsBeqz extends MipsInstruction {
    private final MipsPhyReg reg;
    private final MipsLabel target;

    public MipsBeqz(backend.operand.MipsPhyReg reg, backend.operand.MipsLabel target) {
        this.reg = reg;
        this.target = target;
    }

    @Override
    public String toString() {
        return "beqz " + reg.toString() + ", " + target.toString();
    }
}
