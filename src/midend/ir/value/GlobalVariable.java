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

}
