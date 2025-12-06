package backend.instruction;

import backend.component.MipsFunction;
import backend.operand.MipsPhyReg;

import java.util.HashSet;
import java.util.TreeSet;

public class MipsRet extends MipsInstruction{
    private MipsFunction belongFunc;
    public MipsRet(MipsFunction belongFunc)
    {
        this.belongFunc = belongFunc;
    }

    @Override
    public String toString() {
        StringBuilder retSb = new StringBuilder();
        int stackSize = belongFunc.getTotalStackSize();
        if (stackSize != 0) {
            retSb.append("add $sp, \t$sp,\t").append(stackSize).append("\n");
        }
        if (belongFunc.getName().equals("main")) {
            retSb.append("\tli\t$v0,\t10\n");
            retSb.append("\tsyscall\n\n");
        }
        else {
            HashSet<Integer> calleeSavedRegIndexes = belongFunc.getCalleeSavedRegIndexes();
            int stackOffset = 0;
            for (Integer regIndex : calleeSavedRegIndexes) {
                stackOffset -= 4;
                retSb.append("\t").append("lw ").append(MipsPhyReg.getReg(regIndex)).append(",\t")
                        .append(stackOffset).append("($sp)\n");

            }
            retSb.append("\tjr $ra\n");
        }

        return retSb.toString();
    }
}
