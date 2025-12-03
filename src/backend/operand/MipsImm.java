package backend.operand;

public class MipsImm extends MipsOperand {
    private int num;

    public MipsImm(int num) {
        this.num = num;
    }

    public int getNumber() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    @Override
    public String toString() {
        return Integer.toString(num);
    }
}
