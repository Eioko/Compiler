package backend.instruction;

import backend.operand.MipsOperand;

public class MipsJr extends MipsInstruction {
    private MipsOperand target;

    public MipsJr(MipsOperand target) {
        setTarget(target);
    }

    @Override
    public String toString() {
        return "jr " + target.toString();
    }

    public void setTarget(MipsOperand target){
        addUseReg(this.target, target);
        this.target = target;
    }
    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (target != null && target.equals(oldReg)) {
            setTarget(newReg);
        }
    }
    @Override
    public void replaceUseReg(MipsOperand oldReg, MipsOperand newReg) {
        if (target != null && target.equals(oldReg)) {
            setTarget(newReg);
        }
    }
}
