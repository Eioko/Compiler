package backend.instruction;

import backend.component.MipsFunction;

public class MipsJal extends MipsInstruction{
    private MipsFunction func;

    public MipsJal(MipsFunction func) {
        this.func = func;
    }

    @Override
    public String toString() {
        return "jal " + func.getName();
    }
}
