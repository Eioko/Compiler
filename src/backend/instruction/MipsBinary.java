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
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
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
}
