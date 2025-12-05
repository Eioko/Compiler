package backend.instruction;

import backend.operand.MipsLabel;
import backend.operand.MipsPhyReg;
import backend.operand.MipsOperand;

public class MipsBeqz extends MipsInstruction {
    // 优化改动了这里
    private MipsOperand reg;
    private MipsLabel target;

    public MipsBeqz(MipsOperand reg, backend.operand.MipsLabel target) {
        setSrc(reg);
        this.target = target;
    }

    public void setSrc(MipsOperand reg) {
        addUseReg(this.reg, reg);
        this.reg = reg;
    }

    @Override
    public String toString() {
        return "beqz " + reg.toString() + ", " + target.toString();
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (reg != null && reg.equals(oldReg)) {
            reg = (MipsPhyReg) newReg;
        }
    }
}
