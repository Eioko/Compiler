package backend.instruction;

import backend.component.MipsFunction;
import backend.operand.MipsPhyReg;
import backend.operand.MipsReg;

import java.util.ArrayList;

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
