package midend.ir.constant;

import midend.ir.type.StringType;

public class ConstString extends Constant{
    private String str;

    public ConstString(String str) {
        super(new StringType(str.length()));
        this.str = str;
    }

    public String getValue() {
        return str;
    }
}
