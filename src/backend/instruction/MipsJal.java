package backend.instruction;

import backend.component.MipsFunction;
import backend.operand.MipsPhyReg;
import backend.operand.MipsReg;

import java.util.ArrayList;

public class MipsJal extends MipsInstruction{
    private MipsFunction func;

    public MipsJal(MipsFunction func) {
        this.func = func;
        addDefReg(null, MipsPhyReg.RA);
        addUseReg(null, MipsPhyReg.A0);
        addUseReg(null, MipsPhyReg.A1);
        addUseReg(null, MipsPhyReg.A2);
        addUseReg(null, MipsPhyReg.A3);
    }

    @Override
    public String toString() {
        return "jal " + func.getName();
    }


}
