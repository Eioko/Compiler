package backend.instruction;

import static backend.operand.MipsPhyReg.A0;
import static backend.operand.MipsPhyReg.V0;

public class MipsSyscall extends MipsInstruction {
    private int num;
    public MipsSyscall(int num){
        this.num = num;
        addDefReg(null, V0);
        if(num == 1 || num== 4){
            addUseReg(null, A0);
            addDefReg(null, A0);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("li $v0, ").append(num).append("\n");
        sb.append("\tsyscall");
        return sb.toString();
    }
}
