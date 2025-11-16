package midend.ir.value;

import midend.ir.IrModule;
import midend.ir.constant.Constant;
import midend.ir.type.PointerType;
import midend.ir.type.StringType;

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
        if(((PointerType)getValueType()).getPointeeType() instanceof StringType){
            return this.getName() + " = private unnamed_addr constant " + getUsedValue(0).getValueType()+ " "
                    + getUsedValue(0).toString() + ", align 1";
        }
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

    public Constant getInitValue() {
        return (Constant) getUsedValue(0);
    }
}
