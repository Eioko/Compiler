package midend.ir.instruction;

import midend.ir.type.IntegerType;
import midend.ir.value.BasicBlock;
import midend.ir.value.Value;

public class Icmp extends Instruction {
    public enum IcmpOp {
        LT, GT, LE, GE, EQ, NE
    }

    private IcmpOp op;
    private Value left;
    private Value right;

    public Icmp(int nameNum, IcmpOp op, Value left, Value right, BasicBlock parent) {
        super("%v"+nameNum, new IntegerType(), parent, left, right);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public IcmpOp getOp() {
        return op;
    }

    public Value getLeft() {
        return left;
    }

    public Value getRight() {
        return right;
    }

    @Override
    public String toString() {
        String opStr = switch (op) {
            case LT -> "icmp slt";
            case GT -> "icmp sgt";
            case LE -> "icmp sle";
            case GE -> "icmp sge";
            case EQ -> "icmp eq";
            case NE -> "icmp ne";
        };
        return  getName() + " = " + opStr + " " +
                left.getValueType().toString() + " " + left.getName() + ", " +
                right.getName();
    }
}
