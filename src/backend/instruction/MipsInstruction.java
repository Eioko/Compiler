package backend.instruction;

import backend.operand.MipsOperand;
import backend.operand.MipsPhyReg;
import backend.operand.MipsReg;

import java.util.ArrayList;

public class MipsInstruction {
    private ArrayList<MipsReg> useRegs = new ArrayList<>();
    private ArrayList<MipsReg> defRegs = new ArrayList<>();

    public ArrayList<MipsReg> getUseRegs() {
        return useRegs;
    }
    public ArrayList<MipsReg> getDefRegs() {
        return defRegs;
    }
    public void addDefReg(MipsOperand oldReg, MipsOperand newReg) {
        if (oldReg != null) {
            removeDef(oldReg);
        }
        if (newReg instanceof MipsReg)
        {
            defRegs.add((MipsReg) newReg);
        }
    }

    public void addUseReg(MipsOperand oldReg, MipsOperand newReg) {
        if (oldReg != null) {
            removeUse(oldReg);
        }
        if (newReg instanceof MipsReg)
        {
            useRegs.add((MipsReg) newReg);
        }
    }

    private void removeDef(MipsOperand reg) {
        if (reg instanceof MipsReg) {
            defRegs.remove((MipsReg) reg);
        }
    }

    private void removeUse(MipsOperand reg) {
        if (reg instanceof MipsReg) {
            useRegs.remove((MipsReg) reg);
        }
    }
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {}
    public ArrayList<MipsReg> getWriteRegs()
    {
        return new ArrayList<>(defRegs);
    }
    public void replaceUseReg(MipsOperand oldReg, MipsOperand newReg)
    {}

    public ArrayList<MipsReg> getReadRegs() {
        ArrayList<MipsReg> readRegs = useRegs;
        return readRegs;
    }
}
