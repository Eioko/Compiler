package backend.operand;

public abstract class MipsOperand {
    public boolean isPreColored() {
        return false;
    }

    public boolean needColor() {
        return false;
    }

    public boolean isAllocated() {
        return false;
    }
}
