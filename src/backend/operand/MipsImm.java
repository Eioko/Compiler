package backend.operand;

public class MipsImm extends MipsOperand {
    private final int num;

    public MipsImm(int num) {
        this.num = num;
    }

    public int getNumber() {
        return num;
    }

    @Override
    public String toString() {
        return Integer.toString(num);
    }
}
