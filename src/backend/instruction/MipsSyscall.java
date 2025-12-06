package backend.instruction;

import static backend.operand.MipsPhyReg.V0;

public class MipsSyscall extends MipsInstruction {

    public MipsSyscall(){
        addDefReg(null, V0);
    }
    @Override
    public String toString() {
        return "syscall ";
    }
}
