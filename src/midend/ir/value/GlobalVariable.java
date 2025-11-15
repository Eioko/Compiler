package midend.ir.value;

import midend.ir.IrModule;
import midend.ir.constant.Constant;
import midend.ir.type.PointerType;

public class GlobalVariable extends User {
    private final boolean isConst;
    public GlobalVariable(String name, Constant initVal, boolean isConst) {
        super("@g"+name, new PointerType(initVal.getValueType()), IrModule.getInstance(), initVal);
        this.isConst = isConst;
    }
    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = dso_local ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("global ");
        }
        sb.append(((PointerType)this.getValueType()).getPointeeType());
        sb.append(" ");
        sb.append(getUsedValue(0).toString());
        return sb.toString();
    }
}
