package midend.ir.constant;

import midend.ir.type.IntegerType;

public class ConstInt extends Constant {
    private int value;
    public ConstInt(int value) {
        super(new IntegerType());
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
