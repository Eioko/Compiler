package midend.ir.constant;

import midend.ir.type.StringType;

public class ConstString extends Constant{
    private String str;

    public ConstString(String str, int len) {
        super(new StringType(len));
        this.str = str;
    }

    public String getValue() {
        return str;
    }

    @Override
    public String toString() {
        return "c\"" + str + "\\00\"";
    }

    @Override
    public String getName(){
        return toString();
    }
}
