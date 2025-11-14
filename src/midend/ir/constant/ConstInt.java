package midend.ir.constant;

import midend.ir.type.IntegerType;

public class ConstInt extends Constant {
    public static final ConstInt ZERO = new ConstInt(0);
    private int number;
    public ConstInt(int number) {
        super(new IntegerType());
        this.number = number;
    }
    public int getNumber() {
        return number;
    }
}
