package backend.instruction;

import backend.operand.MipsImm;
import backend.operand.MipsOperand;

public class MipsBinary extends MipsInstruction{
    public enum BinaryOp {
        ADDU, SUBU, MUL, DIV, SLL, MOD
    }
    private BinaryOp op;
    private MipsOperand dest;
    private MipsOperand src1;
    private MipsOperand src2;

    public MipsBinary( BinaryOp op, MipsOperand dest, MipsOperand src1, MipsOperand src2) {
        this.op = op;
        setDst(dest);
        setSrc1(src1);
        setSrc2(src2);
    }

    public void setDst(MipsOperand dst) {
        addDefReg(this.dest, dst);
        this.dest = dst;
    }
    public void setSrc1(MipsOperand src1) {
        addUseReg(this.src1, src1);
        this.src1 = src1;
    }

    public void setSrc2(MipsOperand src2) {
        addUseReg(this.src2, src2);
        this.src2 = src2;
    }
    public BinaryOp getOp() {
        return op;
    }
    public MipsOperand getDst() {
        return dest;
    }
    public MipsOperand getSrc1() {
        return src1;
    }
    public MipsOperand getSrc2() {
        return src2;
    }
    public String toString() {
        if(src2 instanceof MipsImm){
            if(op == BinaryOp.ADDU){
                return "addi\t" + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
            } else if(op == BinaryOp.SUBU){
                MipsImm negImm = new MipsImm(-((MipsImm) src2).getNumber());
                return "addi\t" + dest.toString() + ", " + src1.toString() + ", " + negImm.toString();
            } else if(op == BinaryOp.SLL){
                return "sll\t" + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
            } else {
                throw new RuntimeException("Immediate value is only supported in ADDU and SUBU operations.");
            }
        }else{
            if(op == BinaryOp.ADDU){
                return "addu\t" + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
            } else if(op == BinaryOp.SUBU){
                return "subu\t" + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
            } else if(op == BinaryOp.MUL){
                return "mul\t" + dest.toString() + ", " + src1.toString() + ", " + src2.toString();
            } else if(op == BinaryOp.DIV){
                StringBuilder sb = new StringBuilder();
                sb.append("div\t").append(src1.toString()).append(", ").append(src2.toString()).append("\n");
                sb.append("\tmflo\t").append(dest.toString());
                return sb.toString();
            } else if(op == BinaryOp.MOD){
                StringBuilder sb = new StringBuilder();
                sb.append("div\t").append(src1.toString()).append(", ").append(src2.toString()).append("\n");
                sb.append("\tmfhi\t").append(dest.toString());
                return sb.toString();
            } else {
                throw new RuntimeException("Unsupported binary operation.");
            }
        }
    }

    @Override
    public void replaceReg(MipsOperand oldReg, MipsOperand newReg) {
        if (dest != null && dest.equals(oldReg)) {
            setDst(newReg);
        }
        if (src1 != null && src1.equals(oldReg)) {
            setSrc1(newReg);
        }
        if (src2 != null && src2.equals(oldReg)) {
            setSrc2(newReg);
        }
    }

    @Override
    public void replaceUseReg(MipsOperand oldReg, MipsOperand newReg) {
        if (src1 != null && src1.equals(oldReg)) {
            setSrc1(newReg);
        }
        if (src2 != null && src2.equals(oldReg)) {
            setSrc2(newReg);
        }
    }
}
