package backend.instruction;

import backend.operand.MipsReg;

import java.util.ArrayList;

import static backend.operand.MipsPhyReg.V0;

public class MipsSyscall extends MipsInstruction {

    public MipsSyscall(){
        addDefReg(null, V0);
    }
    @Override
    public String toString() {
        return "syscall ";
    }
    @Override
    public ArrayList<MipsReg> getReadRegs() {
        ArrayList<MipsReg> a = new ArrayList<>();
        a.add(V0);
        return a;
    }
}
