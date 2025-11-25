package backend.instruction;

import backend.operand.MipsOperand;

public class MipsBinary extends MipsInstruction{
    public enum BinaryOp {
        ADDU, SUBU, MUL, DIV, AND, OR, SLT, SLL, SRL, MOD
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


}
