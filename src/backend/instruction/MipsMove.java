package backend.instruction;

import backend.operand.MipsImm;
import backend.operand.MipsLabel;
import backend.operand.MipsOperand;

public class MipsMove extends MipsInstruction {
    private MipsOperand dest;
    private MipsOperand src;

    public MipsMove(MipsOperand dest, MipsOperand src) {
        setDst(dest);
        setSrc(src);
    }

    public void setDst(MipsOperand dst) {
        addDefReg(this.dest, dst);
        this.dest = dst;
    }

    public void setSrc(MipsOperand src) {
        addUseReg(this.src, src);
        this.src = src;
    }

    public MipsOperand getDst() {
        return dest;
    }
    public MipsOperand getSrc() {
        return src;
    }
    @Override
    public String toString() {
        if(src instanceof MipsImm){
            return "li " + dest.toString() + ", " + ((MipsImm) src).getNumber();
        }else if(src instanceof MipsLabel){
            return "la " + dest.toString() + ", " + src.toString();
        }else{
            return "move " + dest.toString() + ", " + src.toString();
        }
    }
    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            setDst(newReg);
        }
        if (src != null && src.equals(oldReg)) {
            setSrc(newReg);
        }
    }
    @Override
    public void replaceUseReg(MipsOperand oldReg, MipsOperand newReg) {
        if (src != null && src.equals(oldReg)) {
            setSrc(newReg);
        }
    }
}
